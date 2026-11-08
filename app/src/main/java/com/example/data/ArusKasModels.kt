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

<<<<<<< HEAD
/**
 * Item Tagihan Wajib dengan Jatuh Tempo, Siklus Habis/Beli Lagi, dan Ceklist 3 Status:
 * - UNPAID: Belum Lunas (Rp 0)
 * - HALF_PAID: Baru Setengah / Dicicil Sebagian
 * - PAID: Sudah Lunas (100%)
 */
data class MandatoryBillItem(
    val id: String,
    val name: String,
    val totalAmount: Long,
    val paidAmount: Long = 0L,
    val dueScheduleLabel: String, // Cth: "Bayar setiap tanggal 1" atau "Kuota habis tgl 15 • Beli lagi tgl 16"
    val dueDayOfMonth: Int = 1,
    val renewalDayOfMonth: Int? = null, // Opsional untuk kuota/token yang ada tgl habis & tgl beli lagi
    val status: String = "UNPAID", // UNPAID, HALF_PAID, PAID
    val billType: String = "KOSAN" // KOSAN, KUOTA, LISTRIK, WIFI, CICILAN, LAINNYA
) {
    val remainingAmount: Long
        get() = (totalAmount - paidAmount).coerceAtLeast(0L)

    val progressFraction: Float
        get() = if (totalAmount > 0L) (paidAmount.toFloat() / totalAmount.toFloat()).coerceIn(0f, 1f) else 0f
}

/**
 * Pintasan item pengeluaran kehidupan sehari-hari yang tidak punya jam/tanggal pasti
 * (seperti beli bensin, beli rokok, makan warteg, kopi, skincare, dll).
 */
data class DailyRealLifeQuickItem(
    val label: String,
    val categoryKey: String,
    val suggestedAmount: Long = 0L,
    val genderFilter: String = "ALL" // ALL, LAKI_LAKI, PEREMPUAN
)

/**
 * Profil Keuangan Pengguna yang diisi sebelum masuk ke Dashboard
 * agar seluruh limit, tagihan wajib, dan pintasan harian langsung siap pakai.
 */
data class UserFinancialProfile(
    val isCompleted: Boolean = false,
    val nickname: String = "",
    val gender: String = "LAKI_LAKI", // LAKI_LAKI, PEREMPUAN
    val livingStatus: String = "NGEKOS", // NGEKOS, RUMAH_ORTU, KELUARGA
    val incomeSourcePrimary: String = "Gaji Bulanan", // Gaji Bulanan, Ngojek / Harian, Usaha / Dagang, Freelance
    val monthlyIncomeAmount: Long = 4_500_000L,
    val paydayDate: Int = 25,
    val dailyHabits: List<String> = listOf("Bensin", "Makan Harian", "Rokok / Kopi", "Kuota Internet"),
    val controlMode: String = "FLEKSIBEL" // FLEKSIBEL (Bebas jam/hari yang penting plafon aman), MINGGUAN, KETAT
)

object PresetCategories {
    // Kategori Khusus Pengeluaran Darurat
    val emergencyExpenseCategory = QuickCategoryItem(
        key = "pakai_darurat",
        label = "Darurat",
        subtitle = "Potong Saldo Darurat",
        frequencyTag = "DARURAT",
        type = "EXPENSE",
        defaultNoteHint = "Keperluan darurat mendesak (ban bocor, obat, dll)...",
=======
object PresetCategories {
    // Kategori Khusus Pengeluaran Darurat (Diinput di tempat yang sama, tapi limit & saldonya tampil khusus di bawah Total Saldo)
    val emergencyExpenseCategory = QuickCategoryItem(
        key = "pakai_darurat",
        label = "Uang Darurat",
        subtitle = "Potong Saldo Darurat Terpisah",
        frequencyTag = "DARURAT",
        type = "EXPENSE",
        defaultNoteHint = "Keperluan darurat mendesak (ban bocor, obat darurat, dll)...",
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
        defaultLimitAmount = 500_000L,
        defaultLimitPeriod = "BULANAN"
    )

<<<<<<< HEAD
    // 6 Pos Pengeluaran Utama Kehidupan Sehari-hari
=======
    // 6 Pos Pengeluaran Utama & Rem Bulanan
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
    val expenseGrid3x3 = listOf(
        QuickCategoryItem(
            key = "tagihan_bulanan",
            label = "Tagihan Wajib",
<<<<<<< HEAD
            subtitle = "Kosan, Kuota, Listrik",
            frequencyTag = "CEKLIST",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Bayar kosan, paket kuota, token listrik, WiFi...",
            defaultLimitAmount = 1_350_000L,
=======
            subtitle = "Kosan, Listrik, WiFi & Kuota",
            frequencyTag = "WAJIB",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Token listrik, WiFi, kosan, paket data...",
            defaultLimitAmount = 1_500_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "kebutuhan_pokok",
<<<<<<< HEAD
            label = "Bensin & Harian",
            subtitle = "Makan, Bensin, Rokok, Air",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Isi bensin, beli rokok, makan siang, air galon...",
            defaultLimitAmount = 1_800_000L,
=======
            label = "Amunisi Harian",
            subtitle = "Makan, Bensin, Dapur & Air",
            frequencyTag = "HARIAN",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Bensin motor, makan siang, galon, sabun...",
            defaultLimitAmount = 2_000_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "rawat_diri",
<<<<<<< HEAD
            label = "Rawat Diri",
            subtitle = "Sabun, Barber, Skincare",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Sabun, sampo, potong rambut, skincare, laundry...",
            defaultLimitAmount = 400_000L,
=======
            label = "Self-Care & Sehat",
            subtitle = "Skincare, Barber & Vitamin",
            frequencyTag = "RAWAT DIRI",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Beli skincare, potong rambut, vitamin...",
            defaultLimitAmount = 500_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "gaya_hidup",
<<<<<<< HEAD
            label = "Jajan & Ngopi",
            subtitle = "Kopi, Cemilan, Nongkrong",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kopi, gorengan, seblak, nongkrong, jajan...",
            defaultLimitAmount = 600_000L,
=======
            label = "Jajan & Healing",
            subtitle = "Kopi, Nongkrong & Hiburan",
            frequencyTag = "BEBAS",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Es kopi susu, seblak, nonton, traktir doi...",
            defaultLimitAmount = 750_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "sosial_darurat",
<<<<<<< HEAD
            label = "Sosial & Ortu",
            subtitle = "Ortu, Kondangan, Patungan",
            frequencyTag = "FLEKSIBEL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kasih ortu, kondangan, patungan temen...",
            defaultLimitAmount = 400_000L,
=======
            label = "Sosial & Berbagi",
            subtitle = "Ortu, Kondangan & Traktir",
            frequencyTag = "SOSIAL",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Kasih ortu, amplop kondangan, patungan...",
            defaultLimitAmount = 500_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            defaultLimitPeriod = "BULANAN"
        ),
        QuickCategoryItem(
            key = "modal_produktif",
<<<<<<< HEAD
            label = "Servis & Kerja",
            subtitle = "Oli, Servis, Modal Kerja",
            frequencyTag = "PRODUKTIF",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Ganti oli motor, servis, alat kerja, modal usaha...",
            defaultLimitAmount = 500_000L,
=======
            label = "Modal & Kerja",
            subtitle = "Servis, Usaha & Upgrade Diri",
            frequencyTag = "PRODUKTIF",
            type = "EXPENSE",
            defaultNoteHint = "Cth: Ganti oli ojol, bahan dagang, alat kerja...",
            defaultLimitAmount = 1_000_000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
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

<<<<<<< HEAD
    val defaultMandatoryBills = listOf(
        MandatoryBillItem(
            id = "bill_kosan",
            name = "Bayar Kosan / Kontrakan",
            totalAmount = 850_000L,
            paidAmount = 0L,
            dueScheduleLabel = "Bayar setiap tanggal 1",
            dueDayOfMonth = 1,
            renewalDayOfMonth = null,
            status = "UNPAID",
            billType = "KOSAN"
        ),
        MandatoryBillItem(
            id = "bill_kuota",
            name = "Paket Kuota Internet",
            totalAmount = 100_000L,
            paidAmount = 0L,
            dueScheduleLabel = "Habis tgl 15 • Beli lagi tgl 16",
            dueDayOfMonth = 15,
            renewalDayOfMonth = 16,
            status = "UNPAID",
            billType = "KUOTA"
        ),
        MandatoryBillItem(
            id = "bill_listrik",
            name = "Token Listrik & Air",
            totalAmount = 200_000L,
            paidAmount = 100_000L,
            dueScheduleLabel = "Isi rutin setiap tanggal 5 & 20",
            dueDayOfMonth = 20,
            renewalDayOfMonth = null,
            status = "HALF_PAID",
            billType = "LISTRIK"
        ),
        MandatoryBillItem(
            id = "bill_wifi",
            name = "WiFi / Langganan Bulanan",
            totalAmount = 200_000L,
            paidAmount = 200_000L,
            dueScheduleLabel = "Jatuh tempo setiap tanggal 10",
            dueDayOfMonth = 10,
            renewalDayOfMonth = null,
            status = "PAID",
            billType = "WIFI"
        )
    )

    val realLifeQuickItems = listOf(
        // Kebutuhan Pokok / Bensin & Harian (Tidak ada jam/tanggal pasti)
        DailyRealLifeQuickItem("⛽ Isi Bensin", "kebutuhan_pokok", 25_000L, "ALL"),
        DailyRealLifeQuickItem("🚬 Beli Rokok", "kebutuhan_pokok", 32_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("🍛 Makan Harian", "kebutuhan_pokok", 20_000L, "ALL"),
        DailyRealLifeQuickItem("🛒 Belanja Dapur/Warung", "kebutuhan_pokok", 35_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("💧 Galon / Gas", "kebutuhan_pokok", 22_000L, "ALL"),
        DailyRealLifeQuickItem("🛵 Ongkos Ojol/Parkir", "kebutuhan_pokok", 15_000L, "ALL"),

        // Gaya Hidup / Jajan & Ngopi
        DailyRealLifeQuickItem("☕ Beli Kopi / Es", "gaya_hidup", 18_000L, "ALL"),
        DailyRealLifeQuickItem("🍜 Jajan / Seblak / Bakso", "gaya_hidup", 20_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("🚬 Rokok / Vape Tongkrongan", "gaya_hidup", 30_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("🍢 Cemilan / Gorengan", "gaya_hidup", 10_000L, "ALL"),
        DailyRealLifeQuickItem("🎮 Nongkrong / Hiburan", "gaya_hidup", 50_000L, "ALL"),

        // Rawat Diri
        DailyRealLifeQuickItem("💈 Potong Rambut / Barber", "rawat_diri", 35_000L, "LAKI_LAKI"),
        DailyRealLifeQuickItem("✨ Skincare / Makeup", "rawat_diri", 75_000L, "PEREMPUAN"),
        DailyRealLifeQuickItem("🧴 Sabun / Sampo / Odol", "rawat_diri", 25_000L, "ALL"),
        DailyRealLifeQuickItem("👕 Laundry Pakaian", "rawat_diri", 20_000L, "ALL"),

        // Sosial & Ortu
        DailyRealLifeQuickItem("🏡 Kasih Orang Tua", "sosial_darurat", 200_000L, "ALL"),
        DailyRealLifeQuickItem("✉️ Amplop Kondangan", "sosial_darurat", 100_000L, "ALL"),
        DailyRealLifeQuickItem("🤝 Traktir / Patungan", "sosial_darurat", 30_000L, "ALL"),

        // Servis & Kerja
        DailyRealLifeQuickItem("🛢️ Ganti Oli Motor", "modal_produktif", 55_000L, "ALL"),
        DailyRealLifeQuickItem("🔧 Servis Kendaraan", "modal_produktif", 100_000L, "ALL"),
        DailyRealLifeQuickItem("📦 Belanja Modal / Kerja", "modal_produktif", 150_000L, "ALL")
    )

    // Minimalist Income Pills
=======
    // Minimalist Income Pills (Smaller than Expense, as requested)
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
    val incomeQuickPills = listOf(
        QuickCategoryItem(
            key = "gaji_bulanan",
            label = "Gaji Utama",
<<<<<<< HEAD
            subtitle = "",
=======
            subtitle = "Pemasukan Pasti",
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Gaji bulanan / bonus kantor..."
        ),
        QuickCategoryItem(
            key = "ngojek_online",
<<<<<<< HEAD
            label = "Ngojek / Harian",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Pendapatan harian / ojol / tip..."
=======
            label = "Ngojek Online",
            subtitle = "Cairan Harian",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Tarik saldo ojol / tip penumpang..."
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
        ),
        QuickCategoryItem(
            key = "hasil_usaha",
            label = "Untung Usaha",
<<<<<<< HEAD
            subtitle = "",
=======
            subtitle = "Omset Dagang",
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Penjualan usaha hari ini..."
        ),
        QuickCategoryItem(
            key = "rejeki_lain",
<<<<<<< HEAD
            label = "Transfer / Bonus",
            subtitle = "",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Cashback, kiriman, bonus..."
=======
            label = "Rejeki / Transfer",
            subtitle = "Pemasukan Lain",
            frequencyTag = "PEMASUKAN",
            type = "INCOME",
            defaultNoteHint = "Cashback, dikasih saudara, dll..."
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
        )
    )

    val defaultStarterWallets = listOf(
        Wallet(
            id = "wallet_bca",
            name = "Bank BCA",
            walletType = "BANK",
<<<<<<< HEAD
            balance = 3_200_000L,
=======
            balance = 4500000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            colorHex = "#0066AE",
            iconKey = "bank_bca"
        ),
        Wallet(
            id = "wallet_mas",
            name = "Bank Mas",
            walletType = "BANK",
<<<<<<< HEAD
            balance = 1_500_000L,
=======
            balance = 2150000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            colorHex = "#F59E0B",
            iconKey = "bank_mas"
        ),
        Wallet(
            id = "wallet_dana",
            name = "DANA",
            walletType = "EWALLET",
<<<<<<< HEAD
            balance = 350_000L,
=======
            balance = 420000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            colorHex = "#118EEA",
            iconKey = "ewallet_dana"
        ),
        Wallet(
            id = "wallet_shopeepay",
            name = "ShopeePay",
            walletType = "EWALLET",
<<<<<<< HEAD
            balance = 250_000L,
=======
            balance = 310000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            colorHex = "#EE4D2D",
            iconKey = "ewallet_shopee"
        ),
        Wallet(
            id = "wallet_gopay",
            name = "GoPay",
            walletType = "EWALLET",
<<<<<<< HEAD
            balance = 400_000L,
=======
            balance = 580000L,
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
            colorHex = "#00AED6",
            iconKey = "ewallet_gopay"
        )
    )
}
