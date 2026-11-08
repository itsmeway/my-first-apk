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
}
