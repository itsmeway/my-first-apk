package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PresetCategories
import com.example.ui.ArusKasViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ArusKas", appName)
  }

  @Test
  fun `verify rupiah formatter and expense categories integrity`() {
    val formatted = ArusKasViewModel.formatRupiah(150000L)
    assertEquals("Rp 150.000", formatted)
    val compact = ArusKasViewModel.formatCompactRupiah(1500000L)
    assertEquals("Rp 1,5 Jt", compact)
    assertEquals(6, PresetCategories.expenseGrid3x3.size)
  }

  @Test
  fun `verify user guide keys and app settings default`() {
    val defaultSettings = com.example.data.AppSettings()
    org.junit.Assert.assertFalse(defaultSettings.hasSeenUserGuide)
    val titleId = com.example.data.AppLocalization.get("guide_title", com.example.data.AppLanguage.INDONESIAN)
    val titleEn = com.example.data.AppLocalization.get("guide_title", com.example.data.AppLanguage.ENGLISH)
    assertEquals("Cara Kerja ArusKas", titleId)
    assertEquals("How ArusKas Works", titleEn)
  }

  @Test
  fun `verify subscription 7k pricing and free trial logic`() {
    val sub = com.example.data.SubscriptionConfig()
    org.junit.Assert.assertEquals("aruskas_pro_monthly_7k", sub.productId)
    org.junit.Assert.assertTrue(sub.isTrialActive)
    org.junit.Assert.assertEquals(30, sub.remainingTrialDays)
    val priceId = com.example.data.AppLocalization.get("paywall_price_headline", com.example.data.AppLanguage.INDONESIAN)
    val priceEn = com.example.data.AppLocalization.get("paywall_price_headline", com.example.data.AppLanguage.ENGLISH)
    assertEquals("Rp 7.000 / Bulan", priceId)
    assertEquals("IDR 7,000 / Month", priceEn)
  }
<<<<<<< HEAD

  @Test
  fun `verify rupiah input formatting with thousands dots and Rp prefix`() {
    val formatted50k = com.example.ui.CurrencyInputFormatter.formatDigits("50000")
    assertEquals("50.000", formatted50k)

    val formatted1_5M = com.example.ui.CurrencyInputFormatter.formatDigits("1500000")
    assertEquals("1.500.000", formatted1_5M)

    val withPrefix = com.example.ui.CurrencyInputFormatter.formatWithPrefix("50000")
    assertEquals("Rp 50.000", withPrefix)

    val parsedLong = com.example.ui.CurrencyInputFormatter.parseToLong("Rp 1.500.000")
    assertEquals(1500000L, parsedLong)
  }

  @Test
  fun `verify mandatory bills checklist states and flexible real life expense logic`() {
    val defaultBills = PresetCategories.defaultMandatoryBills
    org.junit.Assert.assertTrue(defaultBills.any { it.name.contains("Kosan") && it.dueDayOfMonth == 1 })
    org.junit.Assert.assertTrue(defaultBills.any { it.name.contains("Kuota") && it.renewalDayOfMonth != null })

    val kosanBill = defaultBills.first { it.id == "bill_kosan" }.copy(totalAmount = 800_000L, paidAmount = 400_000L, status = "HALF_PAID")
    val kuotaBill = defaultBills.first { it.id == "bill_kuota" }.copy(totalAmount = 100_000L, paidAmount = 100_000L, status = "PAID")
    val spentBills = ArusKasViewModel.calculateSpentForCategoryLimit(
      categoryKey = "tagihan_bulanan",
      period = "BULANAN",
      transactions = emptyList(),
      mandatoryBills = listOf(kosanBill, kuotaBill)
    )
    assertEquals(500_000L, spentBills) // 400k (half kosan) + 100k (paid kuota)

    val quickItems = PresetCategories.realLifeQuickItems
    org.junit.Assert.assertTrue(quickItems.any { it.label.contains("Bensin") })
    org.junit.Assert.assertTrue(quickItems.any { it.label.contains("Rokok") })

    val adaptivePerDay = ArusKasViewModel.calculateFlexibleAdaptiveQuotaPerDay(
      limitAmount = 450_000L,
      spentAmount = 150_000L,
      period = "BULANAN"
    )
    org.junit.Assert.assertTrue(adaptivePerDay > 0L)
  }

  @Test
  fun `verify feature help cloud content database for all core tools`() {
    val totalSaldoHelp = com.example.ui.FeatureHelpDatabase.TOTAL_SALDO
    assertEquals("Total Saldo & Multi-Dompet", totalSaldoHelp.title)
    org.junit.Assert.assertTrue(totalSaldoHelp.summary.isNotBlank())
    org.junit.Assert.assertTrue(totalSaldoHelp.keyPoints.isNotEmpty())

    val billsHelp = com.example.ui.FeatureHelpDatabase.TAGIHAN_WAJIB
    assertEquals("Ceklist Tagihan Wajib & Siklus Jatuh Tempo", billsHelp.title)
    org.junit.Assert.assertTrue(billsHelp.keyPoints.any { it.first.contains("Jatuh Tempo") })

    val pintasanHelp = com.example.ui.FeatureHelpDatabase.PINTASAN_SEHARI_HARI
    org.junit.Assert.assertTrue(pintasanHelp.summary.contains("Bebas Kapan Aja") || pintasanHelp.title.contains("Pintasan"))

    val limitHelp = com.example.ui.FeatureHelpDatabase.KATEGORI_LIMIT
    org.junit.Assert.assertTrue(limitHelp.title.contains("Limit"))
  }
=======
>>>>>>> fdaa85abd0848d84759f2e12b620d84522584107
}
