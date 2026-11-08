package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AppLocalization
import com.example.data.AppSettings
import com.example.data.CurrencyFormatOption
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsBottomSheet(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onExportPdf: () -> Unit,
    onOpenUserGuide: () -> Unit = {},
    onOpenPaywall: () -> Unit = {},
    onDismiss: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentSettings by remember(settings) { mutableStateOf(settings) }
    val lang = currentSettings.language

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppLocalization.get("settings_title", lang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppLocalization.get("settings_subtitle", lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppLocalization.get("btn_close", lang)
                    )
                }
            }

            // PRO SUBSCRIPTION STATUS CARD (Rp 7.000/bln + 30-Day Free Trial)
            val sub = currentSettings.subscription
            val isPro = sub.isProActive
            val isTrial = sub.isTrialActive
            val remainingDays = sub.remainingTrialDays

            val subBadgeText = when {
                isPro -> AppLocalization.get("pro_active_chip", lang)
                isTrial -> String.format(AppLocalization.get("pro_trial_chip", lang), remainingDays)
                else -> AppLocalization.get("pro_expired_chip", lang)
            }
            val subBadgeColor = if (isPro || isTrial) GoldAccent else Color(0xFFFF5252)

            Card(
                onClick = onOpenPaywall,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF141916)
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.3.dp, GoldAccent.copy(alpha = 0.65f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_pro_subscription_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent.copy(alpha = 0.18f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = AppLocalization.get("sec_subscription_title", lang),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = subBadgeColor.copy(alpha = 0.18f),
                                    border = BorderStroke(0.6.dp, subBadgeColor.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = subBadgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = subBadgeColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = AppLocalization.get("sec_subscription_desc", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Buka Langganan",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Quick simulation controls for testing trial expiry & reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val expiredSub = sub.copy(
                            isProActive = false,
                            trialStartDateMillis = System.currentTimeMillis() - 31L * 24 * 60 * 60 * 1000L
                        )
                        val updated = currentSettings.copy(subscription = expiredSub)
                        currentSettings = updated
                        onUpdateSettings(updated)
                        onShowMessage("Masa uji coba disetel kedaluwarsa (31 hari lewat). Layar kunci aktif!")
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🧪 Uji Coba Habis", fontSize = 10.sp, color = Color(0xFFFF8A80))
                }
                OutlinedButton(
                    onClick = {
                        val resetSub = sub.copy(
                            isProActive = false,
                            trialStartDateMillis = System.currentTimeMillis()
                        )
                        val updated = currentSettings.copy(subscription = resetSub)
                        currentSettings = updated
                        onUpdateSettings(updated)
                        onShowMessage("Masa uji coba berhasil direset ke 30 hari penuh!")
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔄 Reset 30 Hari", fontSize = 10.sp, color = EmeraldPrimary)
                }
            }

            // SECTION 1: BAHASA & DAERAH (INDONESIA, SUNDA, JAWA)
            SettingsSectionCard(
                title = AppLocalization.get("sec_language", lang),
                description = AppLocalization.get("sec_language_desc", lang),
                icon = Icons.Default.Language,
                accentColor = EmeraldPrimary
            ) {
                AppLanguage.values().forEach { languageOption ->
                    val isSelected = currentSettings.language == languageOption
                    Surface(
                        onClick = {
                            val updated = currentSettings.copy(language = languageOption)
                            currentSettings = updated
                            onUpdateSettings(updated)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lang_option_${languageOption.code}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = languageOption.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = languageOption.regionLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    val updated = currentSettings.copy(language = languageOption)
                                    currentSettings = updated
                                    onUpdateSettings(updated)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = EmeraldPrimary
                                )
                            )
                        }
                    }
                }
            }

            // SECTION 2: TAMPILAN & FORMAT RUPIAH
            SettingsSectionCard(
                title = AppLocalization.get("sec_appearance", lang),
                description = AppLocalization.get("sec_appearance_desc", lang),
                icon = Icons.Default.Palette,
                accentColor = GoldAccent
            ) {
                // Currency format selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Format Tampilan Nominal",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isFull = currentSettings.currencyFormat == CurrencyFormatOption.FULL_RUPIAH
                        Surface(
                            onClick = {
                                val updated = currentSettings.copy(currencyFormat = CurrencyFormatOption.FULL_RUPIAH)
                                currentSettings = updated
                                onUpdateSettings(updated)
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isFull) GoldAccent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isFull) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Lengkap",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFull) GoldAccent else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Rp 150.000",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val isCompact = currentSettings.currencyFormat == CurrencyFormatOption.COMPACT
                        Surface(
                            onClick = {
                                val updated = currentSettings.copy(currencyFormat = CurrencyFormatOption.COMPACT)
                                currentSettings = updated
                                onUpdateSettings(updated)
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCompact) GoldAccent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isCompact) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Ringkas",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompact) GoldAccent else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Rp 150 Rb / 1,5 Jt",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Sensor Saldo Toggle
                SettingsToggleRow(
                    icon = Icons.Default.VisibilityOff,
                    title = AppLocalization.get("opt_hide_balance", lang),
                    description = AppLocalization.get("opt_hide_balance_desc", lang),
                    checked = currentSettings.hideNominalOnDashboard,
                    onCheckedChange = { checked ->
                        val updated = currentSettings.copy(hideNominalOnDashboard = checked)
                        currentSettings = updated
                        onUpdateSettings(updated)
                    }
                )
            }

            // SECTION 3: KEAMANAN & PRIVASI DATA
            SettingsSectionCard(
                title = AppLocalization.get("sec_security", lang),
                description = AppLocalization.get("sec_security_desc", lang),
                icon = Icons.Default.Security,
                accentColor = SkyBlue
            ) {
                SettingsToggleRow(
                    icon = Icons.Default.Fingerprint,
                    title = AppLocalization.get("opt_app_lock", lang),
                    description = AppLocalization.get("opt_app_lock_desc", lang),
                    checked = currentSettings.isAppLockEnabled,
                    onCheckedChange = { checked ->
                        val updated = currentSettings.copy(isAppLockEnabled = checked)
                        currentSettings = updated
                        onUpdateSettings(updated)
                    }
                )
            }

            // SECTION 4: NOTIFIKASI & RESPON PERANGKAT
            SettingsSectionCard(
                title = AppLocalization.get("sec_notifications", lang),
                description = AppLocalization.get("sec_notifications_desc", lang),
                icon = Icons.Default.NotificationsActive,
                accentColor = EmeraldPrimary
            ) {
                SettingsToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = AppLocalization.get("opt_daily_reminder", lang),
                    description = AppLocalization.get("opt_daily_reminder_desc", lang),
                    checked = currentSettings.dailyReminderNotification,
                    onCheckedChange = { checked ->
                        val updated = currentSettings.copy(dailyReminderNotification = checked)
                        currentSettings = updated
                        onUpdateSettings(updated)
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    title = AppLocalization.get("opt_haptic", lang),
                    description = AppLocalization.get("opt_haptic_desc", lang),
                    checked = currentSettings.soundHapticFeedback,
                    onCheckedChange = { checked ->
                        val updated = currentSettings.copy(soundHapticFeedback = checked)
                        currentSettings = updated
                        onUpdateSettings(updated)
                    }
                )
            }

            // SECTION 5: PENYIMPANAN & CADANGAN CLOUD (STANDAR APLIKASI KEUANGAN)
            SettingsSectionCard(
                title = AppLocalization.get("sec_data_storage", lang),
                description = AppLocalization.get("sec_data_storage_desc", lang),
                icon = Icons.Default.CloudDone,
                accentColor = GoldAccent
            ) {
                SettingsToggleRow(
                    icon = Icons.Default.CloudDone,
                    title = AppLocalization.get("opt_cloud_backup", lang),
                    description = AppLocalization.get("opt_cloud_backup_desc", lang),
                    checked = currentSettings.autoBackupCloud,
                    onCheckedChange = { checked ->
                        val updated = currentSettings.copy(autoBackupCloud = checked)
                        currentSettings = updated
                        onUpdateSettings(updated)
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Cadangkan PDF Laporan Modern
                Surface(
                    onClick = onExportPdf,
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.2.dp, EmeraldPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_modern_pdf_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.22f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocalization.get("opt_export_pdf", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppLocalization.get("opt_export_pdf_desc", lang),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Bersihkan Cache
                Surface(
                    onClick = {
                        onShowMessage(AppLocalization.get("cache_cleared_msg", lang))
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocalization.get("opt_clear_cache", lang),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppLocalization.get("opt_clear_cache_desc", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // SECTION 5.5: PANDUAN CARA KERJA ARUSKAS (USER GUIDE)
            Card(
                onClick = onOpenUserGuide,
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldPrimary.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_user_guide_from_settings")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.25f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = AppLocalization.get("sec_guide_card_title", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppLocalization.get("sec_guide_card_desc", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Buka",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // SECTION 6: TENTANG APLIKASI & KEBIJAKAN PRIVASI
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF041E1B),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.lifesaver_guard_logo_1794106392181),
                                contentDescription = "ArusKas Lifesaver Logo",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Column {
                            Text(
                                text = AppLocalization.get("sec_about", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Financial Lifesaver & Wealth Guardian",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = AppLocalization.get("sec_about_desc", lang),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = AppLocalization.get("sec_about_mission_title", lang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = AppLocalization.get("sec_about_pillar1", lang),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppLocalization.get("sec_about_pillar2", lang),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppLocalization.get("sec_about_pillar3", lang),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppLocalization.get("sec_about_pillar4", lang),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = AppLocalization.get("opt_privacy_policy_desc", lang),
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Done Button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_and_close_settings_button")
            ) {
                Text(
                    text = AppLocalization.get("btn_close", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.18f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmeraldPrimary
            )
        )
    }
}
