package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.AppSettings
import com.example.data.ArusKasRepository
import com.example.data.CategoryLimitConfig
import com.example.data.EmergencyFundConfig
import com.example.data.PresetCategories
import com.example.data.SavingsGoal
import com.example.data.TransactionRecord
import com.example.data.Wallet
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface GuruAdviceState {
    data object Idle : GuruAdviceState
    data object Loading : GuruAdviceState
    data class Success(
        val statusBadge: String,
        val teguran: String,
        val motivasi: String,
        val langkahKonkret: String,
        val colorLevel: String = "GREEN", // GREEN, YELLOW, RED, BLUE
        val saldoCritique: String = "",
        val nominalBoxAnalysis: String = "",
        val categoryCritique: String = ""
    ) : GuruAdviceState
    data class Error(val message: String) : GuruAdviceState
}

class ArusKasViewModel(
    private val repository: ArusKasRepository,
    private val currentUserId: String?
) : ViewModel() {

    private val _sessionSeed = MutableStateFlow(((System.currentTimeMillis() / 1000L) % 9973L).toInt())
    val sessionSeed: StateFlow<Int> = _sessionSeed.asStateFlow()

    private val guruSchema = Schema.obj(
        properties = mapOf(
            "statusBadge" to Schema.string(description = "Label notifikasi pengingat singkat 2-4 kata huruf kapital gaya bestie/gaul, contoh: REM DULU BESTIE!, DOMPET LAGI SEGER, ALARM BOCOR HALUS"),
            "colorLevel" to Schema.string(description = "Pilih satu: GREEN (aman/sehat), YELLOW (waspada/hampir limit), RED (boros/lewat limit/kritis), atau BLUE (fokus target)"),
            "saldoCritique" to Schema.string(description = "1 kalimat singkat gaya temen deket/gaul mengomentari kondisi Total Saldo Siap Pakai vs Tabungan Target & Darurat"),
            "nominalBoxAnalysis" to Schema.string(description = "Analisis keuangan profesional namun disampaikan dengan bahasa gaul/temen deket + sedikit becandaan segar (2 kalimat padat) untuk kotak persegi panjang di bawah masukan nominal"),
            "categoryCritique" to Schema.string(description = "1 kalimat kritik/saran/pengingat bahasa gaul tentang kategori pengeluaran/pemasukan pengguna saat ini"),
            "teguran" to Schema.string(description = "Teguran jujur santai ala sahabat dekat"),
            "motivasi" to Schema.string(description = "Kalimat penyemangat mengejar tabungan target"),
            "langkahKonkret" to Schema.string(description = "1 saran angka praktis hari ini")
        )
    )

    private val guruModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = guruSchema
            }
        )
    }

    private val _localWallets = MutableStateFlow(PresetCategories.defaultStarterWallets)
    private val _localTransactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _localGoals = MutableStateFlow(
        listOf(
            SavingsGoal(
                id = "goal_laptop_utama",
                userId = "local_preview",
                title = "Beli Laptop Impian",
                targetAmount = 10_000_000L,
                savedAmount = 1_250_000L,
                dailyRecommendation = 40_000L,
                note = "Sisihkan rutin dari gaji, ngojek online & usaha",
                iconKey = "laptop"
            )
        )
    )

    val walletsState: StateFlow<UiState<List<Wallet>>> = (
        if (currentUserId != null) {
            repository.observeWallets(currentUserId)
                .map<List<Wallet>, UiState<List<Wallet>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat daftar dompet"))
                }
        } else {
            _localWallets.map<List<Wallet>, UiState<List<Wallet>>> { UiState.Success(it) }
        }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = if (currentUserId == null) UiState.Success(_localWallets.value) else UiState.Loading
    )

    val transactionsState: StateFlow<UiState<List<TransactionRecord>>> = (
        if (currentUserId != null) {
            repository.observeTransactions(currentUserId)
                .map<List<TransactionRecord>, UiState<List<TransactionRecord>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat riwayat transaksi"))
                }
        } else {
            _localTransactions.map<List<TransactionRecord>, UiState<List<TransactionRecord>>> { UiState.Success(it) }
        }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = if (currentUserId == null) UiState.Success(_localTransactions.value) else UiState.Loading
    )

    val goalsState: StateFlow<UiState<List<SavingsGoal>>> = (
        if (currentUserId != null) {
            repository.observeGoals(currentUserId)
                .map<List<SavingsGoal>, UiState<List<SavingsGoal>>> { UiState.Success(it) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    emit(UiState.Error(rootError.message ?: "Gagal memuat target impian"))
                }
        } else {
            _localGoals.map<List<SavingsGoal>, UiState<List<SavingsGoal>>> { UiState.Success(it) }
        }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = if (currentUserId == null) UiState.Success(_localGoals.value) else UiState.Loading
    )

    private val _categoryLimits = MutableStateFlow(PresetCategories.defaultCategoryLimits)
    val categoryLimits: StateFlow<Map<String, CategoryLimitConfig>> = _categoryLimits.asStateFlow()

    private val _emergencyFund = MutableStateFlow(EmergencyFundConfig())
    val emergencyFund: StateFlow<EmergencyFundConfig> = _emergencyFund.asStateFlow()

    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _guruState = MutableStateFlow<GuruAdviceState>(GuruAdviceState.Idle)
    val guruState: StateFlow<GuruAdviceState> = _guruState.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    fun updateLanguage(lang: AppLanguage) {
        _appSettings.value = _appSettings.value.copy(language = lang)
        _actionFeedback.value = when (lang) {
            AppLanguage.ENGLISH -> "Language changed to English!"
            AppLanguage.INDONESIAN -> "Bahasa berhasil diubah ke Bahasa Indonesia!"
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _appSettings.value = newSettings
        _actionFeedback.value = "Pengaturan berhasil disimpan!"
    }

    fun clearFeedback() {
        _actionFeedback.value = null
    }

    fun updateCategoryLimit(
        categoryKey: String,
        categoryLabel: String,
        limitAmount: Long,
        period: String
    ) {
        val cleanLimit = limitAmount.coerceAtLeast(0L)
        _categoryLimits.value = _categoryLimits.value.toMutableMap().apply {
            put(
                categoryKey,
                CategoryLimitConfig(
                    categoryKey = categoryKey,
                    limitAmount = cleanLimit,
                    period = period
                )
            )
        }
        val periodLabel = periodDisplayLabel(period)
        _actionFeedback.value = if (cleanLimit > 0L) {
            "Limit $categoryLabel diatur ke ${formatRupiah(cleanLimit)} ($periodLabel)"
        } else {
            "Limit target untuk $categoryLabel dinonaktifkan"
        }
    }

    fun updateEmergencyFundAndAllocate(
        sourceWallet: Wallet?,
        addedEmergencyAmount: Long,
        newEmergencyLimit: Long,
        limitPeriod: String,
        storagePlaceNote: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val cleanAdd = addedEmergencyAmount.coerceAtLeast(0L)
                val cleanLimit = newEmergencyLimit.coerceAtLeast(0L)
                val currentCfg = _emergencyFund.value

                if (cleanAdd > 0L && sourceWallet != null) {
                    if (currentUserId != null) {
                        val newWalletBalance = sourceWallet.balance - cleanAdd
                        repository.updateWalletBalance(sourceWallet, newWalletBalance)
                    } else {
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == sourceWallet.id) w.copy(balance = w.balance - cleanAdd) else w
                        }
                    }
                }

                _emergencyFund.value = currentCfg.copy(
                    emergencyBalance = currentCfg.emergencyBalance + cleanAdd,
                    emergencyLimit = cleanLimit,
                    limitPeriod = limitPeriod,
                    storagePlaceNote = storagePlaceNote.ifBlank {
                        "Rekening / Kantong Terpisah (Jangan dicampur uang harian)"
                    }
                )

                _actionFeedback.value = if (cleanAdd > 0L && sourceWallet != null) {
                    "${formatRupiah(cleanAdd)} dipotong dari ${sourceWallet.name} ke Saldo Darurat Terpisah! (Limit: ${formatRupiah(cleanLimit)})"
                } else {
                    "Limit Pengeluaran Darurat diperbarui ke ${formatRupiah(cleanLimit)} (${periodDisplayLabel(limitPeriod)})"
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal memperbarui dana darurat: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun seedStarterSetup(
        walletBalances: Map<String, Long>,
        goalTitle: String,
        goalTarget: Long,
        goalDaily: Long,
        goalNote: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null) {
                    repository.seedStarterSetup(
                        walletBalances = walletBalances,
                        initialGoalTitle = goalTitle,
                        initialGoalTarget = goalTarget,
                        initialGoalDaily = goalDaily,
                        initialGoalNote = goalNote
                    )
                } else {
                    _localWallets.value = PresetCategories.defaultStarterWallets.map { preset ->
                        preset.copy(balance = walletBalances[preset.id] ?: preset.balance)
                    }
                    if (goalTitle.isNotBlank() && goalTarget > 0L) {
                        _localGoals.value = listOf(
                            SavingsGoal(
                                id = "goal_laptop_utama",
                                userId = "local_preview",
                                title = goalTitle.trim(),
                                targetAmount = goalTarget,
                                savedAmount = 0L,
                                dailyRecommendation = goalDaily.coerceAtLeast(10_000L),
                                note = goalNote.trim(),
                                iconKey = "laptop"
                            )
                        )
                    }
                }
                _actionFeedback.value = "Dompet (BCA, Bank Mas, DANA, ShopeePay, GoPay) & Target Impian siap dipantau!"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyimpan pengaturan awal: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun recordTransaction(
        wallet: Wallet,
        type: String,
        amount: Long,
        categoryKey: String,
        categoryLabel: String,
        frequencyTag: String,
        note: String,
        onComplete: () -> Unit = {}
    ) {
        if (amount <= 0L) {
            _actionFeedback.value = "Masukkan nominal lebih dari Rp 0"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val isEmergencySpend = categoryKey == "pakai_darurat"
                if (isEmergencySpend) {
                    // Deduct from separated Emergency Balance (since Total Saldo was already deducted when Emergency Balance was set aside)
                    val currentEm = _emergencyFund.value
                    _emergencyFund.value = currentEm.copy(
                        emergencyBalance = (currentEm.emergencyBalance - amount).coerceAtLeast(0L)
                    )
                    val newTx = TransactionRecord(
                        id = "tx_${System.currentTimeMillis()}",
                        userId = currentUserId ?: "local_preview",
                        type = "EXPENSE",
                        amount = amount,
                        categoryKey = categoryKey,
                        categoryLabel = categoryLabel,
                        frequencyTag = "DARURAT",
                        walletId = "pos_darurat_terpisah",
                        walletName = "Saldo Darurat Terpisah",
                        note = note.trim(),
                        transactionDateMillis = System.currentTimeMillis()
                    )
                    _localTransactions.value = listOf(newTx) + _localTransactions.value
                    val formatted = formatRupiah(amount)
                    _actionFeedback.value = "-$formatted dipotong dari Saldo Darurat Terpisah (Tercatat di Limit Darurat)"
                } else {
                    if (currentUserId != null) {
                        repository.recordTransactionAndAdjustWallet(
                            wallet = wallet,
                            type = type,
                            amount = amount,
                            categoryKey = categoryKey,
                            categoryLabel = categoryLabel,
                            frequencyTag = frequencyTag,
                            note = note
                        )
                    } else {
                        val delta = if (type == "INCOME") amount else -amount
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == wallet.id) w.copy(balance = w.balance + delta) else w
                        }
                        val newTx = TransactionRecord(
                            id = "tx_${System.currentTimeMillis()}",
                            userId = "local_preview",
                            type = type,
                            amount = amount,
                            categoryKey = categoryKey,
                            categoryLabel = categoryLabel,
                            frequencyTag = frequencyTag,
                            walletId = wallet.id,
                            walletName = wallet.name,
                            note = note.trim(),
                            transactionDateMillis = System.currentTimeMillis()
                        )
                        _localTransactions.value = listOf(newTx) + _localTransactions.value
                    }
                    val formatted = formatRupiah(amount)
                    _actionFeedback.value = if (type == "INCOME") {
                        "+$formatted masuk ke ${wallet.name} ($categoryLabel)"
                    } else {
                        "-$formatted terpotong dari ${wallet.name} untuk $categoryLabel"
                    }
                }
                onComplete()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal mencatat transaksi: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun addOrUpdateWallet(
        walletId: String?,
        name: String,
        walletType: String,
        balance: Long,
        colorHex: String,
        iconKey: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null) {
                    repository.upsertWallet(
                        walletId = walletId,
                        name = name,
                        walletType = walletType,
                        balance = balance,
                        colorHex = colorHex,
                        iconKey = iconKey
                    )
                } else {
                    if (walletId.isNullOrBlank()) {
                        val newW = Wallet(
                            id = "wallet_${System.currentTimeMillis()}",
                            userId = "local_preview",
                            name = name.trim(),
                            walletType = walletType,
                            balance = balance,
                            colorHex = colorHex,
                            iconKey = iconKey
                        )
                        _localWallets.value = _localWallets.value + newW
                    } else {
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == walletId) {
                                w.copy(
                                    name = name.trim(),
                                    walletType = walletType,
                                    balance = balance,
                                    colorHex = colorHex,
                                    iconKey = iconKey
                                )
                            } else w
                        }
                    }
                }
                _actionFeedback.value = "Dompet $name berhasil disimpan (${formatRupiah(balance)})"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyimpan dompet: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun adjustWalletBalanceDirectly(wallet: Wallet, newBalance: Long) {
        viewModelScope.launch {
            try {
                if (currentUserId != null) {
                    repository.updateWalletBalance(wallet, newBalance)
                } else {
                    _localWallets.value = _localWallets.value.map { w ->
                        if (w.id == wallet.id) w.copy(balance = newBalance) else w
                    }
                }
                _actionFeedback.value = "Saldo ${wallet.name} diperbarui menjadi ${formatRupiah(newBalance)}"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal mengubah saldo: ${e.localizedMessage}"
            }
        }
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            try {
                if (currentUserId != null) {
                    repository.deleteWallet(wallet.id)
                } else {
                    _localWallets.value = _localWallets.value.filterNot { it.id == wallet.id }
                }
                _actionFeedback.value = "Dompet ${wallet.name} dihapus"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus dompet: ${e.localizedMessage}"
            }
        }
    }

    fun deleteTransaction(tx: TransactionRecord, wallets: List<Wallet>) {
        viewModelScope.launch {
            try {
                if (tx.categoryKey == "pakai_darurat") {
                    _localTransactions.value = _localTransactions.value.filterNot { it.id == tx.id }
                    val currentEm = _emergencyFund.value
                    _emergencyFund.value = currentEm.copy(
                        emergencyBalance = currentEm.emergencyBalance + tx.amount
                    )
                    _actionFeedback.value = "Catatan darurat dihapus & Saldo Darurat Terpisah dikembalikan"
                    return@launch
                }
                val matchingWallet = wallets.find { it.id == tx.walletId }
                if (currentUserId != null) {
                    repository.deleteTransactionAndRevertWallet(tx, matchingWallet)
                } else {
                    _localTransactions.value = _localTransactions.value.filterNot { it.id == tx.id }
                    if (matchingWallet != null) {
                        val revertDelta = if (tx.type == "INCOME") -tx.amount else tx.amount
                        _localWallets.value = _localWallets.value.map { w ->
                            if (w.id == matchingWallet.id) w.copy(balance = w.balance + revertDelta) else w
                        }
                    }
                }
                _actionFeedback.value = "Catatan dihapus & saldo ${tx.walletName} dikembalikan"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus catatan: ${e.localizedMessage}"
            }
        }
    }

    fun addSavingsGoal(
        title: String,
        targetAmount: Long,
        dailyRecommendation: Long,
        note: String,
        iconKey: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null) {
                    repository.createSavingsGoal(
                        title = title,
                        targetAmount = targetAmount,
                        savedAmount = 0L,
                        dailyRecommendation = dailyRecommendation,
                        note = note,
                        iconKey = iconKey
                    )
                } else {
                    val newGoal = SavingsGoal(
                        id = "goal_${System.currentTimeMillis()}",
                        userId = "local_preview",
                        title = title.trim(),
                        targetAmount = targetAmount,
                        savedAmount = 0L,
                        dailyRecommendation = dailyRecommendation,
                        note = note.trim(),
                        iconKey = iconKey
                    )
                    _localGoals.value = _localGoals.value + newGoal
                }
                _actionFeedback.value = "Impian '$title' berhasil ditambahkan!"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menambah impian: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun allocateSavingsToGoal(
        goal: SavingsGoal,
        wallet: Wallet,
        amount: Long,
        note: String
    ) {
        if (amount <= 0L) {
            _actionFeedback.value = "Nominal tabungan harus lebih dari Rp 0"
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                if (currentUserId != null) {
                    repository.allocateToGoalFromWallet(
                        goal = goal,
                        wallet = wallet,
                        amount = amount,
                        note = note
                    )
                } else {
                    _localWallets.value = _localWallets.value.map { w ->
                        if (w.id == wallet.id) w.copy(balance = w.balance - amount) else w
                    }
                    _localGoals.value = _localGoals.value.map { g ->
                        if (g.id == goal.id) g.copy(savedAmount = g.savedAmount + amount) else g
                    }
                    val newTx = TransactionRecord(
                        id = "tx_${System.currentTimeMillis()}",
                        userId = "local_preview",
                        type = "SAVINGS_ALLOCATION",
                        amount = amount,
                        categoryKey = "tabungan_impian",
                        categoryLabel = "Tabungan: ${goal.title}",
                        frequencyTag = "TABUNGAN",
                        walletId = wallet.id,
                        walletName = wallet.name,
                        note = note.ifBlank { "Setor tabungan ke ${goal.title}" },
                        transactionDateMillis = System.currentTimeMillis()
                    )
                    _localTransactions.value = listOf(newTx) + _localTransactions.value
                }
                _actionFeedback.value = "Mantap! ${formatRupiah(amount)} dari ${wallet.name} masuk ke tabungan ${goal.title}"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menyetor tabungan: ${e.localizedMessage}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            try {
                if (currentUserId != null) {
                    repository.deleteSavingsGoal(goal.id)
                } else {
                    _localGoals.value = _localGoals.value.filterNot { it.id == goal.id }
                }
                _actionFeedback.value = "Target '${goal.title}' dihapus"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionFeedback.value = "Gagal menghapus target: ${e.localizedMessage}"
            }
        }
    }

    fun rotateSessionSeed() {
        _sessionSeed.value = (_sessionSeed.value + 137 + ((System.currentTimeMillis() % 97L).toInt())) % 10000
    }

    fun requestGuruFinancialEvaluation(
        wallets: List<Wallet>,
        transactions: List<TransactionRecord>,
        goals: List<SavingsGoal>
    ) {
        rotateSessionSeed()
        val currentSeed = _sessionSeed.value
        viewModelScope.launch {
            _guruState.value = GuruAdviceState.Loading
            try {
                val totalBalance = wallets.sumOf { it.balance }
                val totalGoalSaved = goals.sumOf { it.savedAmount }
                val emergencyBalance = _emergencyFund.value.emergencyBalance
                val walletBreakdown = wallets.joinToString(", ") { "${it.name}: ${formatRupiah(it.balance)}" }

                val todayStart = getStartOfDayMillis()
                val monthStart = getStartOfMonthMillis()

                val todayExpenses = transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= todayStart }
                val todayExpenseSum = todayExpenses.sumOf { it.amount }
                val mendadakExpenses = transactions.filter {
                    it.type == "EXPENSE" && (it.frequencyTag == "MENDADAK" || it.categoryKey == "gaya_hidup_bebas") && it.transactionDateMillis >= monthStart
                }
                val mendadakSum = mendadakExpenses.sumOf { it.amount }
                val monthlyExpenseSum = transactions.filter { it.type == "EXPENSE" && it.transactionDateMillis >= monthStart }.sumOf { it.amount }
                val monthlyIncomeSum = transactions.filter { it.type == "INCOME" && it.transactionDateMillis >= monthStart }.sumOf { it.amount }

                val goalsSummary = if (goals.isEmpty()) {
                    "Belum ada tabungan target spesifik."
                } else {
                    goals.joinToString("; ") {
                        "${it.title} (Terkumpul ${formatRupiah(it.savedAmount)} dari ${formatRupiah(it.targetAmount)}, rekomendasi ${formatRupiah(it.dailyRecommendation)}/hari)"
                    }
                }

                val recentNotes = transactions.take(5).joinToString(" | ") {
                    "${it.type} ${formatRupiah(it.amount)} (${it.categoryLabel} via ${it.walletName}: '${it.note}')"
                }

                val styleAngles = listOf(
                    "Gaya temen nongkrong deket yang perhatian tapi suka ngeledek halus kalau boros",
                    "Gaya sahabat karib yang paham banget perjuangan cari duit (gaji, ngojek, usaha) sambil kasih jokes receh",
                    "Gaya bestie finansial yang jujur blak-blakan tapi tetep bikin senyum dan semangat nabung",
                    "Gaya bro/bestie tongkrongan yang ngingetin masa depan biar nggak boncos di tanggal tua"
                )
                val chosenAngle = styleAngles[currentSeed % styleAngles.size]

                val prompt = """
                    Lo adalah temen nongkrong deket anak Gen Z Indonesia yang ngerti banget keuangan dan peduli sama keseharian temen lo.
                    JANGAN PERNAH pake bahasa baku (jangan pake kata "Anda", "Kamu", "Sebaiknya", "Telah"). Wajib pake bahasa sehari-hari gaul Gen Z ("woy", "lo/gue", "cuy", "bro", "bestie", "jujurly", "boncos").
                    Contoh gaya bicara yang wajib lo tiru: "woy, pengeluaran jajan lo kebanyakan bulan ini, udah setara minum kopi 15 cup, sehat dikit lah dompet sama lambung lo!"
                    Variasi sesi ke-$currentSeed ($chosenAngle).

                    Data keuangan real-time temen lo:
                    - Saldo Siap Pakai: ${formatRupiah(totalBalance)} ($walletBreakdown)
                    - Tabungan Target Terpisah: ${formatRupiah(totalGoalSaved)} ($goalsSummary)
                    - Simpanan Darurat Terpisah: ${formatRupiah(emergencyBalance)}
                    - Pengeluaran Hari Ini: ${formatRupiah(todayExpenseSum)} (${todayExpenses.size} transaksi)
                    - Pengeluaran Bulan Ini: ${formatRupiah(monthlyExpenseSum)} vs Pemasukan Bulan Ini: ${formatRupiah(monthlyIncomeSum)}
                    - Pengeluaran Gaya Hidup / Jajan Bulan Ini: ${formatRupiah(mendadakSum)}
                    - 5 Transaksi Terakhir: $recentNotes

                    Hasilkan JSON sesuai skema:
                    - statusBadge: 2-3 kata kapital singkat.
                    - colorLevel: "GREEN" (aman), "YELLOW" (waspada/rem dikit), "RED" (boros/boncos), "BLUE" (fokus target).
                    - nominalBoxAnalysis: 1-2 kalimat bahasa gaul anak Gen Z ("woy, lo...") yang mengkritik/mengingatkan/memotivasi keseharian pengguna berdasarkan angka di atas (misal konversi ke cup kopi/seblak/bensin/ngojek/target laptop).
                    - saldoCritique, categoryCritique, teguran, motivasi, langkahKonkret: isi singkat gaya gaul Gen Z.
                """.trimIndent()

                val response = try {
                    withTimeout(30_000L) {
                        guruModel.generateContent(prompt)
                    }
                } catch (firstTimeout: TimeoutCancellationException) {
                    Log.w(TAG, "First Bestie AI attempt timed out at 30s, retrying once...")
                    withTimeout(45_000L) {
                        guruModel.generateContent(prompt)
                    }
                }
                val jsonString = response.text ?: throw IllegalStateException("Respons AI kosong.")
                val json = JSONObject(jsonString)

                val teguranText = json.optString("teguran", "Jangan gatel checkout mulu bestie, cek lagi arus kasmu hari ini!")
                val motivasiText = json.optString("motivasi", "Dikit demi sedikit lama-lama kebeli juga targetmu bro!")
                val langkahText = json.optString("langkahKonkret", "Sisihkan minimal Rp 25.000 hari ini ke Tabungan Target.")

                _guruState.value = GuruAdviceState.Success(
                    statusBadge = json.optString("statusBadge", "PENGINGAT BESTIE KE-$currentSeed"),
                    teguran = teguranText,
                    motivasi = motivasiText,
                    langkahKonkret = langkahText,
                    colorLevel = json.optString("colorLevel", "GREEN"),
                    saldoCritique = json.optString("saldoCritique", teguranText),
                    nominalBoxAnalysis = json.optString("nominalBoxAnalysis", "$teguranText $langkahText"),
                    categoryCritique = json.optString("categoryCritique", motivasiText)
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Bestie evaluation timed out", e)
                _guruState.value = GuruAdviceState.Error("Analisis AI lagi antre, menampilkan analisis instan bestie.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Bestie evaluation failed", e)
                _guruState.value = GuruAdviceState.Error(
                    e.localizedMessage ?: "Menampilkan analisis instan bestie."
                )
            }
        }
    }

    companion object {
        private const val TAG = "ArusKasViewModel"

        fun formatRupiah(amount: Long): String {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getNumberInstance(localeID)
            return if (amount < 0) {
                "-Rp ${numberFormat.format(-amount)}"
            } else {
                "Rp ${numberFormat.format(amount)}"
            }
        }

        fun formatCompactRupiah(amount: Long): String {
            val abs = kotlin.math.abs(amount)
            val prefix = if (amount < 0) "-Rp " else "Rp "
            return when {
                abs >= 1_000_000_000L -> String.format(Locale("in", "ID"), "%s%.1f M", prefix, abs / 1_000_000_000.0)
                abs >= 1_000_000L -> String.format(Locale("in", "ID"), "%s%.1f Jt", prefix, abs / 1_000_000.0)
                abs >= 1_000L -> String.format(Locale("in", "ID"), "%s%d Rb", prefix, abs / 1_000L)
                else -> "$prefix$abs"
            }
        }

        fun getStartOfDayMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getStartOfWeekMillis(): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -6)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun getStartOfMonthMillis(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun periodDisplayLabel(period: String): String {
            return when (period) {
                "HARIAN" -> "Per Hari"
                "MINGGUAN" -> "Per Minggu"
                "BEBAS" -> "Target Bebas"
                else -> "Per Bulan"
            }
        }

        fun calculateSpentForCategoryLimit(
            categoryKey: String,
            period: String,
            transactions: List<TransactionRecord>
        ): Long {
            val startMillis = when (period) {
                "HARIAN" -> getStartOfDayMillis()
                "MINGGUAN" -> getStartOfWeekMillis()
                "BEBAS" -> 0L
                else -> getStartOfMonthMillis()
            }
            return transactions
                .filter { it.type == "EXPENSE" && it.categoryKey == categoryKey && it.transactionDateMillis >= startMillis }
                .sumOf { it.amount }
        }
    }
}
