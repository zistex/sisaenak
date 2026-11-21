package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.FavoriteStoreEntity
import com.example.data.ProductEntity
import com.example.data.StoreStatusType
import com.example.data.formatRupiah
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodProductImage
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.SecondaryGreenButton
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
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberSoft
import com.example.ui.theme.LiveRedSoft
import com.example.ui.theme.StarYellow
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray

@Composable
fun AccountTabScreen(
    uiState: SisaEnakUiState,
    onOpenWallet: () -> Unit,
    onOpenOrdersTab: () -> Unit,
    onOpenVouchers: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenViewHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMerchantScanQr: () -> Unit,
    onOpenMerchantDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgLightGray),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card: Siti Rahma + Email Terverifikasi
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BgWhite,
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_avatar_siti),
                        contentDescription = "Foto Profil ${uiState.userPrefs.userName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.userPrefs.userName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Terverifikasi",
                                tint = FreshGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Email Terverifikasi",
                                style = MaterialTheme.typography.labelMedium,
                                color = FreshGreenDark
                            )
                        }
                        Text(
                            text = uiState.userPrefs.userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }

            // 3 Large Box Menus: Dompet LUDES, Pesanan Saya, Vouchers & Promo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccountQuickBox(
                    title = "Dompet\nLUDES",
                    subtitle = formatRupiah(uiState.userPrefs.walletBalanceRp),
                    icon = Icons.Filled.AccountBalanceWallet,
                    tint = FreshGreenDark,
                    onClick = onOpenWallet,
                    testTag = "account_wallet_box",
                    modifier = Modifier.weight(1f)
                )
                AccountQuickBox(
                    title = "Pesanan\nSaya",
                    subtitle = "${uiState.orders.size} transaksi",
                    icon = Icons.Filled.ReceiptLong,
                    tint = ActionOrange,
                    onClick = onOpenOrdersTab,
                    testTag = "account_orders_box",
                    modifier = Modifier.weight(1f)
                )
                AccountQuickBox(
                    title = "Vouchers\n& Promo",
                    subtitle = "${uiState.vouchers.size} aktif",
                    icon = Icons.Filled.ConfirmationNumber,
                    tint = ActionOrangeDark,
                    onClick = onOpenVouchers,
                    testTag = "account_vouchers_box",
                    modifier = Modifier.weight(1f)
                )
            }

            // Mandatory Ordered Menu List:
            // 1. FAVORIT - 12 item disimpan
            // 2. RIWAYAT DILIHAT
            // 3. PENGATURAN
            // (NO ALAMAT PENGIRIMAN!)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BgWhite,
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    AccountMenuRow(
                        icon = Icons.Filled.Favorite,
                        iconTint = LiveRed,
                        title = "Favorit - ${uiState.totalFavoritesCount} item disimpan",
                        subtitle = "Warung favorit & produk yang dilike",
                        onClick = onOpenFavorites,
                        testTag = "account_menu_favorit"
                    )
                    HorizontalDivider(color = SurfaceBorder)
                    AccountMenuRow(
                        icon = Icons.Filled.History,
                        iconTint = ActionOrange,
                        title = "Riwayat Dilihat",
                        subtitle = "Produk yang pernah kamu lihat",
                        onClick = onOpenViewHistory,
                        testTag = "account_menu_riwayat"
                    )
                    HorizontalDivider(color = SurfaceBorder)
                    AccountMenuRow(
                        icon = Icons.Filled.Settings,
                        iconTint = FreshGreenDark,
                        title = "Pengaturan",
                        subtitle = "Notifikasi, Bantuan WA, Jadi Penjual",
                        onClick = onOpenSettings,
                        testTag = "account_menu_pengaturan"
                    )
                }
            }

            // Menu Khusus Penjual: Scan QR Pesanan & Kelola Warung
            val pendingQrCount = uiState.orders.count { it.status == com.example.data.OrderStatus.BELUM_DIAMBIL.name }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BgWhite,
                border = BorderStroke(1.5.dp, FreshGreenBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ActionOrangeSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = "Menu Penjual",
                                tint = ActionOrangeDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Menu Penjual",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack
                            )
                            Text(
                                text = if (pendingQrCount > 0) {
                                    "$pendingQrCount pesanan menunggu scan QR pembeli"
                                } else {
                                    "Scan QR pesanan agar transaksi selesai & sukses"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = FreshGreenDark
                            )
                        }
                    }

                    PrimaryOrangeButton(
                        text = "Scan QR Pesanan Pembeli",
                        onClick = onOpenMerchantScanQr,
                        testTag = "account_menu_scan_qr_penjual",
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedButton(
                        onClick = onOpenMerchantDashboard,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, FreshGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FreshGreenDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("account_menu_kelola_warung")
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kelola Warung & Tayangkan Kantong", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AccountQuickBox(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BgWhite,
        border = BorderStroke(1.dp, SurfaceBorder),
        shadowElevation = 1.dp,
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, lineHeight = 17.sp),
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AccountMenuRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp)
            .testTag(testTag)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextBlack)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextGray)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGray
        )
    }
}

/**
 * Sub-screen 1: FAVORIT (A. Warung Favorit, B. Produk yang Dilike)
 */
@Composable
fun FavoritesScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onRemoveFavoriteStore: (String) -> Unit,
    onToggleProductFavorite: (String) -> Unit,
    onBuyProduct: (ProductEntity) -> Unit
) {
    val productsById = uiState.products.associateBy { it.id }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Favorit (${uiState.totalFavoritesCount} item disimpan)",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // A. Warung Favorit
            item {
                Text(
                    text = "A. Warung Favorit (${uiState.favoriteStores.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack
                )
            }

            if (uiState.favoriteStores.isEmpty()) {
                item {
                    EmptySectionCard("Belum ada warung favorit.")
                }
            } else {
                items(uiState.favoriteStores, key = { it.id }) { store ->
                    FavoriteStoreCard(
                        store = store,
                        onRemoveHeart = { onRemoveFavoriteStore(store.id) },
                        onBuyClick = {
                            val linked = productsById[store.linkedProductId]
                            if (linked != null) onBuyProduct(linked)
                        }
                    )
                }
            }

            // B. Produk yang Dilike
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "B. Produk yang Dilike (${uiState.favoriteProducts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack
                )
            }

            if (uiState.favoriteProducts.isEmpty()) {
                item {
                    EmptySectionCard("Belum ada produk yang dilike. Tekan ikon hati di Beranda!")
                }
            } else {
                items(uiState.favoriteProducts, key = { it.id }) { product ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BgWhite),
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodProductImage(
                                imageKey = product.imageKey,
                                customImageUri = product.customImageUri,
                                contentDescription = product.title,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack,
                                    maxLines = 2
                                )
                                Text(
                                    text = formatRupiah(product.discountPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ActionOrange
                                )
                            }
                            IconButton(onClick = { onToggleProductFavorite(product.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Hapus dari Favorit",
                                    tint = LiveRed
                                )
                            }
                            if (product.stock > 0) {
                                Button(
                                    onClick = { onBuyProduct(product) },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = ActionOrange),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.heightIn(min = 42.dp)
                                ) {
                                    Text("BELI", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteStoreCard(
    store: FavoriteStoreEntity,
    onRemoveHeart: () -> Unit,
    onBuyClick: () -> Unit
) {
    val statusType = runCatching { StoreStatusType.valueOf(store.statusType) }
        .getOrDefault(StoreStatusType.OPEN_LIMITED)

    val (statusLabel, badgeBg, badgeColor, showBuyButton) = when (statusType) {
        StoreStatusType.OPEN_LIMITED -> Quad(
            "BUKA MALAM INI - Sisa ${store.remainingStock}",
            ActionOrangeSoft,
            ActionOrangeDark,
            true
        )
        StoreStatusType.OPEN_MANY -> Quad(
            "BUKA - Sisa ${store.remainingStock}",
            FreshGreenSoft,
            FreshGreenDark,
            true
        )
        StoreStatusType.CLOSED -> Quad(
            "TUTUP - SISA HABIS",
            BgLightGray,
            TextGray,
            false
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FoodProductImage(
                    imageKey = store.imageKey,
                    contentDescription = store.storeName,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.storeName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextBlack
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${store.rating}", style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextGray, modifier = Modifier.size(15.dp))
                        Text(store.distanceText, style = MaterialTheme.typography.bodyMedium, color = TextGray)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(50), color = badgeBg) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                IconButton(onClick = onRemoveHeart) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Hapus warung favorit",
                        tint = LiveRed
                    )
                }
            }

            if (showBuyButton) {
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryOrangeButton(
                    text = "BELI",
                    onClick = onBuyClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Sub-screen 2: RIWAYAT DILIHAT
 */
@Composable
fun ViewHistoryScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onBuyAgain: (ProductEntity) -> Unit
) {
    val productsMap = uiState.products.associateBy { it.id }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Riwayat Dilihat",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.viewedHistory, key = { it.productId }) { entry ->
                val product = productsMap[entry.productId]
                if (product != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BgWhite),
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodProductImage(
                                imageKey = product.imageKey,
                                customImageUri = product.customImageUri,
                                contentDescription = product.title,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack,
                                    maxLines = 2
                                )
                                Text(
                                    text = formatRupiah(product.discountPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ActionOrange
                                )
                                Text(
                                    text = entry.viewedLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }
                            Button(
                                onClick = { onBuyAgain(product) },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = ActionOrange),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                modifier = Modifier.heightIn(min = 44.dp)
                            ) {
                                Text("BELI LAGI", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sub-screen 3: PENGATURAN
 */
@Composable
fun SettingsScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenLanguageSettings: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenMerchantMode: () -> Unit,
    onLogout: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    val prefs = uiState.userPrefs
    val activeNotifCount = listOf(
        prefs.notifPromoEnabled,
        prefs.notifOrderEnabled,
        prefs.notifLiveEnabled,
        prefs.notifWalletEnabled
    ).count { it }
    val notifSummary = if (prefs.notifPromoEnabled) {
        "$activeNotifCount saluran aktif • Pengingat ${prefs.notificationHour} WIB"
    } else {
        "$activeNotifCount saluran aktif • Pengingat harian nonaktif"
    }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Pengaturan",
                onBack = onBack
            )
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
                // Section: Preferensi Aplikasi
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Preferensi Aplikasi",
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
                            SettingsItemRow(
                                icon = Icons.Filled.Notifications,
                                title = "Notifikasi",
                                value = notifSummary,
                                onClick = onOpenNotificationSettings,
                                testTag = "settings_notifications_row"
                            )
                            HorizontalDivider(color = SurfaceBorder)
                            SettingsItemRow(
                                icon = Icons.Filled.Language,
                                title = "Bahasa",
                                value = appLanguageDisplayName(prefs.appLanguage),
                                onClick = onOpenLanguageSettings,
                                testTag = "settings_language_row"
                            )
                        }
                    }
                }

                // Section: Bantuan & Legal
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Bantuan & Legal",
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
                            SettingsItemRow(
                                icon = Icons.Filled.SupportAgent,
                                title = "Bantuan (WA Admin)",
                                value = "Chat WhatsApp Admin LUDES",
                                onClick = {
                                    val waIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://wa.me/6281234567890?text=Halo%20Admin%20LUDES,%20saya%20butuh%20bantuan")
                                    )
                                    runCatching { context.startActivity(waIntent) }
                                        .onFailure { onShowMessage("WA Admin LUDES: 0812-3456-7890") }
                                },
                                testTag = "settings_support_row"
                            )
                            HorizontalDivider(color = SurfaceBorder)
                            SettingsItemRow(
                                icon = Icons.Filled.PrivacyTip,
                                title = "Kebijakan Privasi",
                                value = "Privasi Data, Ambil Sendiri & Refund",
                                onClick = onOpenPrivacyPolicy,
                                testTag = "settings_privacy_row"
                            )
                        }
                    }
                }

                // Jadi Penjual - Tombol Oranye
                PrimaryOrangeButton(
                    text = "Jadi Penjual • Kelola Warung Saya",
                    onClick = onOpenMerchantMode,
                    testTag = "become_merchant_button",
                    modifier = Modifier.fillMaxWidth()
                )

                // Keluar - Merah
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, LiveRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LiveRed),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar", style = MaterialTheme.typography.labelLarge)
                }

                Text(
                    text = "Versi Aplikasi 1.2.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = BgWhite,
            title = { Text("Keluar dari Akun?") },
            text = { Text("Kamu sedang masuk sebagai ${uiState.userPrefs.userName} (${uiState.userPrefs.userEmail}).") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Ya, Keluar", color = LiveRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable { onClick() }
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = FreshGreenSoft,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = FreshGreenDark, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = TextBlack)
            Text(value, style = MaterialTheme.typography.bodySmall, color = TextGray)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextGray)
    }
}

@Composable
fun WalletScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onTopUp: (Int) -> Unit,
    onRequestWithdraw: (Int, String) -> Unit,
    onSimulateWithdrawComplete: (String) -> Unit
) {
    val balance = uiState.userPrefs.walletBalanceRp
    var selectedPreset by remember { mutableIntStateOf(25000) } // -1 = Semua Saldo
    val effectiveAmount = if (selectedPreset == -1) balance else selectedPreset
    val canWithdraw = effectiveAmount >= 10000 && balance >= effectiveAmount

    val destinations = remember {
        listOf(
            "BCA •••• 4821 (Siti Rahma)",
            "BRI •••• 1109 (Siti Rahma)",
            "GoPay • 0812••••7890",
            "OVO • 0812••••7890"
        )
    }
    var selectedDestination by remember { mutableStateOf(destinations.first()) }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Dompet LUDES",
                onBack = onBack
            )
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
                // Card Saldo Utama
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = FreshGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Saldo LUDES Kamu",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupiah(balance),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bisa dipakai belanja lagi atau ditarik ke rekening bank & e-wallet tanpa biaya admin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }

                // Card Tarik Saldo (Withdraw Request - Tanpa Ketik)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = ActionOrangeSoft,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountBalance,
                                        contentDescription = null,
                                        tint = ActionOrangeDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Tarik Saldo (Withdraw Request)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextBlack
                                )
                                Text(
                                    text = "Pilih nominal & tujuan pencairan tanpa ketik",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray
                                )
                            }
                        }

                        Text(
                            text = "Pilih Nominal Penarikan",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextBlack
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                10000 to "Rp10rb",
                                25000 to "Rp25rb",
                                50000 to "Rp50rb",
                                -1 to "Semua"
                            ).forEach { (nominalValue, label) ->
                                val isSelected = selectedPreset == nominalValue
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) ActionOrangeSoft else BgLightGray,
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) ActionOrange else SurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedPreset = nominalValue }
                                        .testTag("withdraw_nominal_$nominalValue")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) ActionOrangeDark else TextBlack
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Tujuan Pencairan",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextBlack
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            destinations.forEach { dest ->
                                val isSelected = selectedDestination == dest
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) FreshGreenSoft else BgLightGray,
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) FreshGreen else SurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedDestination = dest }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = dest,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = TextBlack
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "Dipilih",
                                                tint = FreshGreenDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgLightGray,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Nominal Tarik", style = MaterialTheme.typography.bodySmall, color = TextGray)
                                    Text(
                                        text = formatRupiah(effectiveAmount),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextBlack
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Biaya Admin", style = MaterialTheme.typography.bodySmall, color = TextGray)
                                    Text("Gratis (Rp0)", style = MaterialTheme.typography.labelMedium, color = FreshGreenDark)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Estimasi Masuk", style = MaterialTheme.typography.bodySmall, color = TextGray)
                                    Text("Maks. 1x24 jam", style = MaterialTheme.typography.labelMedium, color = TextBlack)
                                }
                            }
                        }

                        if (!canWithdraw) {
                            Text(
                                text = if (balance < 10000) {
                                    "Minimal penarikan Rp10.000. Saldo kamu saat ini ${formatRupiah(balance)}."
                                } else {
                                    "Saldo kamu belum cukup untuk penarikan ${formatRupiah(effectiveAmount)}."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = LiveRed
                            )
                        }

                        PrimaryOrangeButton(
                            text = "Ajukan Tarik Saldo • ${formatRupiah(effectiveAmount)}",
                            onClick = { onRequestWithdraw(effectiveAmount, selectedDestination) },
                            enabled = canWithdraw,
                            testTag = "wallet_withdraw_submit_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Isi Saldo Cepat
                Text(
                    text = "Isi Saldo Cepat (Tanpa Ketik)",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextBlack
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(10000, 25000, 50000).forEach { nominal ->
                        SecondaryGreenButton(
                            text = "+${formatRupiah(nominal)}",
                            onClick = { onTopUp(nominal) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Riwayat Penarikan (Withdraw)
                Text(
                    text = "Riwayat Penarikan (${uiState.withdrawRequests.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextBlack
                )

                if (uiState.withdrawRequests.isEmpty()) {
                    EmptySectionCard("Belum ada riwayat permintaan tarik saldo.")
                } else {
                    uiState.withdrawRequests.forEach { req ->
                        val isDone = req.status == "Berhasil Dicairkan"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = req.id,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextBlack
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (isDone) FreshGreenSoft else StatusAmberSoft
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Schedule,
                                                contentDescription = null,
                                                tint = if (isDone) FreshGreenDark else StatusAmber,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = req.status,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isDone) FreshGreenDark else StatusAmber
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = req.destinationLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextGray,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = formatRupiah(req.amountRp),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = ActionOrangeDark
                                    )
                                }

                                if (!isDone) {
                                    OutlinedButton(
                                        onClick = { onSimulateWithdrawComplete(req.id) },
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, FreshGreen),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 44.dp)
                                    ) {
                                        Text(
                                            text = "Cairkan Sekarang (Simulasi Bank)",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = FreshGreenDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun VouchersScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onUseVoucher: () -> Unit
) {
    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Vouchers & Promo",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.vouchers, key = { it.code }) { voucher ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, FreshGreenBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(shape = RoundedCornerShape(50), color = FreshGreenSoft) {
                            Text(
                                text = voucher.code,
                                style = MaterialTheme.typography.labelMedium,
                                color = FreshGreenDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(voucher.title, style = MaterialTheme.typography.titleSmall, color = TextBlack)
                        Text(voucher.description, style = MaterialTheme.typography.bodySmall, color = TextGray)
                        Spacer(modifier = Modifier.height(10.dp))
                        PrimaryOrangeButton(
                            text = "Pakai di Beranda",
                            onClick = onUseVoucher,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySectionCard(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BgWhite,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(18.dp)
        )
    }
}
