package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Wallet(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val walletType: String = "BANK", // BANK, EWALLET, CASH
    val balance: Long = 0L,
    val colorHex: String = "#10B981",
    val iconKey: String = "bank_bca",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TransactionRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "EXPENSE", // EXPENSE, INCOME, SAVINGS_ALLOCATION
    val amount: Long = 0L,
    val categoryKey: String = "",
    val categoryLabel: String = "",
    val frequencyTag: String = "HARIAN", // HARIAN, MINGGUAN, BULANAN, MENDADAK, PEMASUKAN, TABUNGAN
    val walletId: String = "",
    val walletName: String = "",
    val note: String = "",
    val transactionDateMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavingsGoal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmount: Long = 0L,
    val savedAmount: Long = 0L,
    val dailyRecommendation: Long = 0L,
    val note: String = "",
    val iconKey: String = "laptop",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class QuickCategoryItem(
    val key: String,
    val label: String,
    val subtitle: String,
    val frequencyTag: String,
    val type: String, // EXPENSE or INCOME
    val defaultNoteHint: String,
    val defaultLimitAmount: Long = 1_000_000L,
    val defaultLimitPeriod: String = "BULANAN" // BULANAN, MINGGUAN, HARIAN, BEBAS
)

data class CategoryLimitConfig(
    val categoryKey: String,
    val limitAmount: Long,
    val period: String = "BULANAN" // BULANAN, MINGGUAN, HARIAN, BEBAS
)

data class EmergencyFundConfig(
    val emergencyBalance: Long = 1_000_000L,
    val emergencyLimit: Long = 500_000L,
    val limitPeriod: String = "BULANAN", // BULANAN, MINGGUAN, HARIAN, BEBAS
    val storagePlaceNote: String = "Rekening / Kantong Terpisah (Jangan dicampur uang harian)"
)

object PresetCategories {
    // Kategori Khusus Pengeluaran Darurat (Diinput di tempat yang sama, tapi limit & saldonya tampil khusus di bawah Total Saldo)
    val emergencyExpenseCategory = QuickCategoryItem(
        key = "pakai_darurat",
        label = "Uang Darurat",
        subtitle = "Potong Saldo Darurat Terpisah",
        frequencyTag = "DARURAT",
        type = "EXPENSE",
        defaultNoteHint = "Keperluan darurat mendesak (ban bocor, obat darurat, dll)...",
        defaultLimitAmount = 500_000L,
        defaultLimitPeriod = "BULANAN"
    )

    // 6 Pos Pengeluaran Utama & Rem Bulanan
    val expenseGrid3x3 = listOf(
        QuickCategoryItem(
            key = "tagihan_bulanan",
            label = "Tagihan Wajib",
            subtitle = "Kosan, Listrik, WiFi & Kuota",
            frequencyTag = "WAJIB",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Token listrik, WiFi, kosan, paket data...",
            defaultLimitAmount = 1_500_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "kebutuhan_pokok",
            label = "Amunisi Harian",
            subtitle = "Makan, Bensin, Dapur & Air",
            frequencyTag = "HARIAN",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Bensin motor, makan siang, galon, sabun...",
            defaultLimitAmount = 2_000_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "rawat_diri",
            label = "Self-Care & Sehat",
            subtitle = "Skincare, Barber & Vitamin",
            frequencyTag = "RAWAT DIRI",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Beli skincare, potong rambut, vitamin...",
            defaultLimitAmount = 500_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "gaya_hidup",
            label = "Jajan & Healing",
            subtitle = "Kopi, Nongkrong & Hiburan",
            frequencyTag = "BEBAS",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Es kopi susu, seblak, nonton, traktir doi...",
            defaultLimitAmount = 750_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "sosial_darurat",
            label = "Sosial & Berbagi",
            subtitle = "Ortu, Kondangan & Traktir",
            frequencyTag = "SOSIAL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kasih ortu, amplop kondangan, patungan...",
            defaultLimitAmount = 500_000L,
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "modal_produktif",
            label = "Modal & Kerja",
            subtitle = "Servis, Usaha & Upgrade Diri",
            frequencyTag = "PRODUKTIF",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Ganti oli ojol, bahan dagang, alat kerja...",
            defaultLimitAmount = 1_000_000L,
            defaultLimitPeriod = "BULANAN"
        )
    )

    val defaultCategoryLimits: Map<String, CategoryLimitConfig> = expenseGrid3x3.associate { item ->
        item.key to CategoryLimitConfig(
            categoryKey = item.key,
            limitAmount = item.defaultLimitAmount,
            period = item.defaultLimitPeriod
        )
    }

    // Minimalist Income Pills (Smaller than Expense, as requested)
    val incomeQuickPills = listOf(
        QuickCategoryItem(
            key = "gaji_bulanan",
            label = "Gaji Utama",
            subtitle = "Pemasukan Pasti",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Gaji bulanan / bonus kantor..."
        ),
        QuickCategoryItem(
            key = "ngojek_online",
            label = "Ngojek Online",
            subtitle = "Cairan Harian",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Tarik saldo ojol / tip penumpang..."
        ),
        QuickCategoryItem(
            key = "hasil_usaha",
            label = "Untung Usaha",
            subtitle = "Omset Dagang",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Penjualan usaha hari ini..."
        ),
        QuickCategoryItem(
            key = "rejeki_lain",
            label = "Rejeki / Transfer",
            subtitle = "Pemasukan Lain",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Cashback, dikasih saudara, dll..."
        )
    )

    val defaultStarterWallets = listOf(
        Wallet(
            id = "wallet_bca",
            name = "Bank BCA",
            walletType = "BANK",
            balance = 4500000L,
            colorHex = "#0066AE",
            iconKey = "bank_bca"
        ),
        Wallet(
            id = "wallet_mas",
            name = "Bank Mas",
            walletType = "BANK",
            balance = 2150000L,
            colorHex = "#F59E0B",
            iconKey = "bank_mas"
        ),
        Wallet(
            id = "wallet_dana",
            name = "DANA",
            walletType = "EWALLET",
            balance = 420000L,
            colorHex = "#118EEA",
            iconKey = "ewallet_dana"
        ),
        Wallet(
            id = "wallet_shopeepay",
            name = "ShopeePay",
            walletType = "EWALLET",
            balance = 310000L,
            colorHex = "#EE4D2D",
            iconKey = "ewallet_shopee"
        ),
        Wallet(
            id = "wallet_gopay",
            name = "GoPay",
            walletType = "EWALLET",
            balance = 580000L,
            colorHex = "#00AED6",
            iconKey = "ewallet_gopay"
        )
    )
}
