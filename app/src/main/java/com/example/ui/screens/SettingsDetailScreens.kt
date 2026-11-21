package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SisaEnakUiState
import com.example.ui.components.SubScreenTopBar
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.ActionOrangeDark
import com.example.ui.theme.ActionOrangeSoft
import com.example.ui.theme.BgLightGray
import com.example.ui.theme.BgWhite
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenBorder
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted

/**
 * Full-page 1: Pengaturan Notifikasi
 */
@Composable
fun NotificationSettingsScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onSaveSettings: (
        promoEnabled: Boolean,
        orderEnabled: Boolean,
        liveEnabled: Boolean,
        walletEnabled: Boolean,
        hour: String
    ) -> Unit
) {
    val prefs = uiState.userPrefs
    var promoEnabled by remember(prefs.notifPromoEnabled) { mutableStateOf(prefs.notifPromoEnabled) }
    var orderEnabled by remember(prefs.notifOrderEnabled) { mutableStateOf(prefs.notifOrderEnabled) }
    var liveEnabled by remember(prefs.notifLiveEnabled) { mutableStateOf(prefs.notifLiveEnabled) }
    var walletEnabled by remember(prefs.notifWalletEnabled) { mutableStateOf(prefs.notifWalletEnabled) }
    var selectedHour by remember(prefs.notificationHour) { mutableStateOf(prefs.notificationHour) }

    val activeChannelsCount = listOf(promoEnabled, orderEnabled, liveEnabled, walletEnabled).count { it }
    val hasChanges = promoEnabled != prefs.notifPromoEnabled ||
        orderEnabled != prefs.notifOrderEnabled ||
        liveEnabled != prefs.notifLiveEnabled ||
        walletEnabled != prefs.notifWalletEnabled ||
        selectedHour != prefs.notificationHour

    val scheduleSlots = remember {
        listOf(
            Triple("17.00", "Sore • Persiapan makan malam", "Warung mulai siapkan stok hemat"),
            Triple("18.00", "Petang • Diskon awal dibuka", "Pilihan menu masih paling lengkap"),
            Triple("19.00", "Malam • Puncak promo LUDES", "Direkomendasikan"),
            Triple("20.00", "Malam • Penghabisan sebelum tutup", "Diskon maksimal hingga 70%")
        )
    }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Pengaturan Notifikasi",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = BgWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onSaveSettings(promoEnabled, orderEnabled, liveEnabled, walletEnabled, selectedHour)
                            onBack()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FreshGreenDark,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("save_notification_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasChanges) "Simpan Pengaturan Notifikasi" else "Selesai",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Overview Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = FreshGreenSoft,
                            border = BorderStroke(1.dp, FreshGreenBorder),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = FreshGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Saluran Pemberitahuan",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextBlack
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (activeChannelsCount > 0) FreshGreenSoft else BgLightGray
                                ) {
                                    Text(
                                        text = "$activeChannelsCount/4 aktif",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (activeChannelsCount > 0) FreshGreenDark else TextGray,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pilih info apa saja yang ingin kamu terima agar tidak ketinggalan kantong hemat maupun batas ambil pesanan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }
                }

                // Channel Toggles Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Jenis Notifikasi",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            NotificationToggleRow(
                                icon = Icons.Filled.LocalOffer,
                                iconTint = ActionOrangeDark,
                                iconBg = ActionOrangeSoft,
                                title = "Pengingat Harian Promo Hemat",
                                subtitle = "Kabari saat warung terdekat mulai diskon hingga 70% sebelum ludes",
                                checked = promoEnabled,
                                onCheckedChange = { promoEnabled = it },
                                testTag = "notif_toggle_promo"
                            )
                            HorizontalDivider(color = SurfaceBorder)
                            NotificationToggleRow(
                                icon = Icons.Filled.ReceiptLong,
                                iconTint = FreshGreenDark,
                                iconBg = FreshGreenSoft,
                                title = "Status Pesanan & Kode QR",
                                subtitle = "Info instan saat pesanan siap diambil & pengingat batas waktu 15 menit",
                                checked = orderEnabled,
                                onCheckedChange = { orderEnabled = it },
                                testTag = "notif_toggle_order"
                            )
                            HorizontalDivider(color = SurfaceBorder)
                            NotificationToggleRow(
                                icon = Icons.Filled.LiveTv,
                                iconTint = Color(0xFFDC2626),
                                iconBg = Color(0xFFFEF2F2),
                                title = "Siaran Live Warung Favorit",
                                subtitle = "Notifikasi saat toko langganan mulai live dengan harga spesial",
                                checked = liveEnabled,
                                onCheckedChange = { liveEnabled = it },
                                testTag = "notif_toggle_live"
                            )
                            HorizontalDivider(color = SurfaceBorder)
                            NotificationToggleRow(
                                icon = Icons.Filled.AccountBalanceWallet,
                                iconTint = Color(0xFF1D4ED8),
                                iconBg = Color(0xFFEFF6FF),
                                title = "Dompet & Pengembalian Saldo",
                                subtitle = "Pemberitahuan refund otomatis ke Saldo LUDES & status pencairan dana",
                                checked = walletEnabled,
                                onCheckedChange = { walletEnabled = it },
                                testTag = "notif_toggle_wallet"
                            )
                        }
                    }
                }

                // Daily Promo Schedule Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.alpha(if (promoEnabled) 1f else 0.55f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jam Pengingat Harian",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextBlack
                        )
                        Text(
                            text = if (promoEnabled) "$selectedHour WIB" else "Pengingat Nonaktif",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (promoEnabled) FreshGreenDark else TextGray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = FreshGreenDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pilih jam rutin pengingat kantong hemat di sekitar lokasimu:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }

                            scheduleSlots.forEach { (hour, title, hint) ->
                                val isSelected = selectedHour == hour
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) FreshGreenSoft else BgLightGray,
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) FreshGreen else SurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = promoEnabled) { selectedHour = hour }
                                        .testTag("notif_hour_$hour")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) FreshGreenDark else BgWhite,
                                            border = if (isSelected) null else BorderStroke(1.dp, SurfaceBorder)
                                        ) {
                                            Text(
                                                text = "$hour WIB",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.ExtraBold
                                                ),
                                                color = if (isSelected) Color.White else TextBlack,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = TextBlack
                                            )
                                            Text(
                                                text = hint,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) FreshGreenDark else TextGray
                                            )
                                        }
                                        SelectionRadioBadge(selected = isSelected)
                                    }
                                }
                            }
                        }
                    }
                }

                // Helpful Note
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Notifikasi status QR dan batas ambil 15 menit tetap diprioritaskan saat kamu memiliki pesanan aktif agar dana tidak terlewat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconBg,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FreshGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SurfaceBorder
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

/**
 * Full-page 2: Pengaturan Bahasa
 */
private data class LanguageOption(
    val code: String,
    val badge: String,
    val name: String,
    val regionLabel: String,
    val badgeText: String?,
    val previewGreeting: String,
    val previewAction: String,
    val previewPickupRule: String
)

fun appLanguageDisplayName(code: String): String = when (code) {
    "en" -> "English (EN)"
    "su" -> "Basa Sunda (ID)"
    else -> "Bahasa Indonesia (ID)"
}

@Composable
fun LanguageSettingsScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onApplyLanguage: (code: String, displayLabel: String) -> Unit
) {
    val languages = remember {
        listOf(
            LanguageOption(
                code = "id",
                badge = "ID",
                name = "Bahasa Indonesia",
                regionLabel = "Indonesia • Bahasa standar & utama LUDES",
                badgeText = "Direkomendasikan",
                previewGreeting = "Makanan Enak Harga Hemat, Sebelum Ludes",
                previewAction = "Pesan Ambil Sendiri Sekarang",
                previewPickupRule = "Ambil sendiri di warung tanpa ongkir"
            ),
            LanguageOption(
                code = "en",
                badge = "EN",
                name = "English",
                regionLabel = "International • Clean English labels",
                badgeText = null,
                previewGreeting = "Delicious Surplus Meals at Smart Prices",
                previewAction = "Reserve Self-Pickup Bag",
                previewPickupRule = "Self-pickup only at partner stalls • Zero delivery fee"
            ),
            LanguageOption(
                code = "su",
                badge = "SU",
                name = "Basa Sunda",
                regionLabel = "Bandung & Jawa Barat • Gaya sapaan lokal",
                badgeText = "Khas Bandung",
                previewGreeting = "Katuangan Raos Harga Mirah, Sateuacan Ludes",
                previewAction = "Pesen Candak Nyalira Ayeuna",
                previewPickupRule = "Dicandak nyalira ka warung • Teu aya ongkir"
            )
        )
    }

    val currentCode = uiState.userPrefs.appLanguage
    var selectedCode by remember(currentCode) { mutableStateOf(currentCode) }
    val selectedOption = languages.find { it.code == selectedCode } ?: languages.first()
    val hasChanges = selectedCode != currentCode

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Bahasa Aplikasi",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = BgWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onApplyLanguage(selectedOption.code, appLanguageDisplayName(selectedOption.code))
                            onBack()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FreshGreenDark,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("save_language_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasChanges) "Terapkan ${selectedOption.name}" else "Bahasa Sudah Aktif",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Status Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = FreshGreenSoft,
                            border = BorderStroke(1.dp, FreshGreenBorder),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = null,
                                    tint = FreshGreenDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Preferensi Bahasa & Wilayah",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Saat ini aktif: ${appLanguageDisplayName(currentCode)}. Pilihan disimpan otomatis ke akun kamu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }
                }

                // Language Options
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pilih Bahasa",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    languages.forEach { option ->
                        val isSelected = selectedCode == option.code
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) FreshGreenSoft else BgWhite,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) FreshGreen else SurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedCode = option.code }
                                .testTag("lang_option_${option.code}")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) FreshGreenDark else BgLightGray,
                                    border = if (isSelected) null else BorderStroke(1.dp, SurfaceBorder),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = option.badge,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.ExtraBold
                                            ),
                                            color = if (isSelected) Color.White else TextBlack
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = option.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = TextBlack
                                        )
                                        option.badgeText?.let { tag ->
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = if (isSelected) BgWhite else ActionOrangeSoft
                                            ) {
                                                Text(
                                                    text = tag,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    ),
                                                    color = if (isSelected) FreshGreenDark else ActionOrangeDark,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = option.regionLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) FreshGreenDark else TextGray
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))
                                SelectionRadioBadge(selected = isSelected)
                            }
                        }
                    }
                }

                // Live Preview Card
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pratinjau Tampilan",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Language,
                                    contentDescription = null,
                                    tint = FreshGreenDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Contoh gaya bahasa (${selectedOption.name})",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = FreshGreenDark
                                )
                            }
                            HorizontalDivider(color = SurfaceBorder)
                            Text(
                                text = selectedOption.previewGreeting,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextBlack
                            )
                            Text(
                                text = selectedOption.previewPickupRule,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ActionOrangeSoft,
                                border = BorderStroke(1.dp, ActionOrange.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = selectedOption.previewAction,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ActionOrangeDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-page 3: Kebijakan Privasi & Ketentuan Layanan
 */
private enum class PolicyTopicFilter(val label: String) {
    ALL("Semua"),
    DATA("Data & Lokasi"),
    PICKUP("Ambil Sendiri"),
    PAYMENT("Pembayaran & Refund")
}

private data class PolicySectionItem(
    val id: String,
    val number: String,
    val title: String,
    val summary: String,
    val topic: PolicyTopicFilter,
    val icon: ImageVector,
    val points: List<String>
)

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTopic by remember { mutableStateOf(PolicyTopicFilter.ALL) }
    var expandedIds by remember { mutableStateOf(setOf("p1", "p2", "p3", "p4", "p5")) }

    val sections = remember {
        listOf(
            PolicySectionItem(
                id = "p1",
                number = "01",
                title = "Data Akun & Titik Lokasi",
                summary = "Penggunaan data profil & jarak warung terdekat",
                topic = PolicyTopicFilter.DATA,
                icon = Icons.Filled.PersonSearch,
                points = listOf(
                    "Nama dan email hanya dipakai untuk identitas akun, riwayat pesanan, dan validasi kode QR pengambilan.",
                    "Titik lokasi Bandung yang kamu atur di peta digunakan semata-mata untuk menghitung jarak warung terdekat secara akurat.",
                    "LUDES tidak melacak lokasi di latar belakang dan tidak membagikan data pribadi kamu kepada pihak ketiga di luar kebutuhan transaksi."
                )
            ),
            PolicySectionItem(
                id = "p2",
                number = "02",
                title = "Aturan Layanan Ambil Sendiri",
                summary = "Khusus pickup di lokasi warung tanpa kurir",
                topic = PolicyTopicFilter.PICKUP,
                icon = Icons.Filled.Storefront,
                points = listOf(
                    "Seluruh pesanan di LUDES wajib diambil langsung oleh pembeli di alamat warung atau toko mitra (tidak ada kurir & tidak memakai alamat rumah).",
                    "Bebas ongkos kirim (Rp0 ongkir) untuk semua transaksi.",
                    "Pastikan datang sesuai slot jam pengambilan yang kamu pilih dan tunjukkan kode QR pesanan kepada kasir warung."
                )
            ),
            PolicySectionItem(
                id = "p3",
                number = "03",
                title = "Pembayaran Aman & Larangan COD",
                summary = "Seluruh transaksi diproses aman melalui aplikasi",
                topic = PolicyTopicFilter.PAYMENT,
                icon = Icons.Filled.Security,
                points = listOf(
                    "Pembayaran wajib diselesaikan melalui aplikasi LUDES (QRIS, GoPay, OVO, DANA, Transfer Bank, atau Saldo LUDES).",
                    "Tidak melayani pembayaran tunai di warung (COD) demi keamanan stok dan transparansi.",
                    "Dana pembayaran diteruskan ke warung mitra pada H+1 setelah kode QR pengambilan berhasil dipindai kasir (potongan fee platform 10%)."
                )
            ),
            PolicySectionItem(
                id = "p4",
                number = "04",
                title = "Batal Otomatis 15 Menit & Refund",
                summary = "Perlindungan dana dan pencegahan makanan terbuang",
                topic = PolicyTopicFilter.PAYMENT,
                icon = Icons.Filled.Schedule,
                points = listOf(
                    "Jika pesanan belum diambil hingga melewati 15 menit dari akhir slot jam pengambilan, sistem membatalkan pesanan secara otomatis.",
                    "100% total dana pembayaran langsung dikembalikan ke Saldo LUDES kamu dan dapat digunakan kembali atau ditarik ke rekening/e-wallet.",
                    "Stok kantong makanan dikembalikan ke Beranda agar masih bisa diselamatkan pembeli lain sebelum ludes."
                )
            ),
            PolicySectionItem(
                id = "p5",
                number = "05",
                title = "Kendali Privasi & Keamanan Akun",
                summary = "Hak pengguna atas preferensi dan bantuan admin",
                topic = PolicyTopicFilter.DATA,
                icon = Icons.Filled.Lock,
                points = listOf(
                    "Kamu dapat mengubah lokasi acuan, preferensi notifikasi, dan bahasa aplikasi kapan saja melalui menu Pengaturan.",
                    "Untuk permintaan penghapusan data akun atau kendala transaksi, hubungi Admin Resmi LUDES melalui WhatsApp."
                )
            )
        )
    }

    val visibleSections = remember(selectedTopic) {
        if (selectedTopic == PolicyTopicFilter.ALL) sections else sections.filter { it.topic == selectedTopic }
    }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Kebijakan Privasi",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = BgWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FreshGreenDark,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("privacy_understand_button")
                    ) {
                        Text(
                            text = "Saya Mengerti",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero Trust Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FreshGreenSoft,
                                    border = BorderStroke(1.dp, FreshGreenBorder),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.VerifiedUser,
                                            contentDescription = null,
                                            tint = FreshGreenDark,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Privasi & Ketentuan LUDES",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = TextBlack
                                    )
                                    Text(
                                        text = "Diperbarui November 2026 • Versi 1.2",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = FreshGreenDark
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Dokumen ini menjelaskan bagaimana LUDES mengelola data akun, acuan lokasi peta, sistem Ambil Sendiri tanpa COD, serta perlindungan refund otomatis ke Saldo LUDES.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }

                // Topic Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PolicyTopicFilter.entries.forEach { topic ->
                        val active = selectedTopic == topic
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BgWhite,
                            border = BorderStroke(
                                width = if (active) 1.2.dp else 0.8.dp,
                                color = if (active) FreshGreenDark else SurfaceBorder
                            ),
                            modifier = Modifier
                                .heightIn(min = 38.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { selectedTopic = topic }
                                .testTag("privacy_filter_${topic.name}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = topic.label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (active) FreshGreenDark else TextGray
                                )
                            }
                        }
                    }
                }

                // Expandable Policy Cards
                visibleSections.forEach { section ->
                    val isExpanded = expandedIds.contains(section.id)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedIds = if (isExpanded) {
                                            expandedIds - section.id
                                        } else {
                                            expandedIds + section.id
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FreshGreenSoft,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = section.icon,
                                            contentDescription = null,
                                            tint = FreshGreenDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${section.number}. ${section.title}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextBlack
                                    )
                                    Text(
                                        text = section.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGray
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                                    tint = TextGray
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    HorizontalDivider(color = SurfaceBorder)
                                    section.points.forEach { point ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 7.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(FreshGreen)
                                            )
                                            Text(
                                                text = point,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 13.5.sp,
                                                    lineHeight = 20.sp
                                                ),
                                                color = TextBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Support / Data Request Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Butuh Bantuan Terkait Privasi atau Transaksi?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextBlack
                        )
                        Text(
                            text = "Tim Admin LUDES siap membantu verifikasi akun, refund, atau permintaan data.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                        OutlinedButton(
                            onClick = {
                                val waIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/6281234567890?text=Halo%20Admin%20LUDES,%20saya%20ingin%20bertanya%20tentang%20Kebijakan%20Privasi")
                                )
                                runCatching { context.startActivity(waIntent) }
                                    .onFailure { onShowMessage("WA Admin LUDES: 0812-3456-7890") }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, FreshGreenBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = FreshGreenSoft,
                                contentColor = FreshGreenDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SupportAgent,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hubungi Admin LUDES via WhatsApp",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionRadioBadge(selected: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (selected) FreshGreenDark else Color.Transparent,
        border = BorderStroke(1.5.dp, if (selected) FreshGreenDark else TextMuted),
        modifier = Modifier.size(22.dp)
    ) {
        if (selected) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Dipilih",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
