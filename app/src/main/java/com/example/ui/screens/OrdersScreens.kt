package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.formatRupiah
import com.example.data.formatRupiahShort
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodProductImage
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.QrCodeCard
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
import com.example.ui.theme.LiveRedSoft
import com.example.ui.theme.StarYellow
import com.example.ui.theme.StatusAmberSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray

@Composable
fun OrdersTabScreen(
    uiState: SisaEnakUiState,
    onOpenOrderDetail: (OrderEntity) -> Unit,
    onOpenRating: (OrderEntity) -> Unit,
    onExploreHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Aktif, 1 = Riwayat

    val activeOrders = uiState.orders.filter { it.status == OrderStatus.BELUM_DIAMBIL.name }
    val historyOrders = uiState.orders.filter { it.status != OrderStatus.BELUM_DIAMBIL.name }
    val displayedOrders = if (selectedTab == 0) activeOrders else historyOrders

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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Pesanan Saya",
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Large 2-tab selector: Aktif | Riwayat
            Surface(
                shape = RoundedCornerShape(50),
                color = BgWhite,
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tabs = listOf(
                        "Aktif (${activeOrders.size})",
                        "Riwayat (${historyOrders.size})"
                    )
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) ActionOrange else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { selectedTab = idx }
                                .testTag(if (idx == 0) "orders_tab_aktif" else "orders_tab_riwayat")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else TextGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (displayedOrders.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = FreshGreen,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedTab == 0) "Belum ada pesanan aktif" else "Belum ada riwayat pesanan",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yuk selamatkan makanan enak di Beranda dengan ambil sendiri gratis!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        PrimaryOrangeButton(
                            text = "Cari Makanan Sekarang",
                            onClick = onExploreHome
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(displayedOrders, key = { it.id }) { order ->
                        OrderListCard(
                            order = order,
                            onClick = { onOpenOrderDetail(order) },
                            onRateClick = { onOpenRating(order) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderListCard(
    order: OrderEntity,
    onClick: () -> Unit,
    onRateClick: () -> Unit
) {
    val statusEnum = runCatching { OrderStatus.valueOf(order.status) }.getOrDefault(OrderStatus.BELUM_DIAMBIL)
    val (badgeBg, badgeText) = when (statusEnum) {
        OrderStatus.BELUM_DIAMBIL -> StatusAmberSoft to ActionOrangeDark
        OrderStatus.SUDAH_DIAMBIL -> FreshGreenSoft to FreshGreenDark
        OrderStatus.BATAL -> LiveRedSoft to LiveRed
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite),
        border = BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}")
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesanan ${order.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextBlack
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBg
                ) {
                    Text(
                        text = statusEnum.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                FoodProductImage(
                    imageKey = order.imageKey,
                    contentDescription = order.productTitle,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextBlack,
                        maxLines = 2
                    )
                    Text(
                        text = order.storeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FreshGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = ActionOrange,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ambil ${order.pickupSlot} • ${formatRupiah(order.totalPaid)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextBlack
                        )
                    }
                }
            }

            if (statusEnum == OrderStatus.BELUM_DIAMBIL) {
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryOrangeButton(
                    text = "Tampilkan QR & Status Ambil",
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (statusEnum == OrderStatus.SUDAH_DIAMBIL && order.rating == 0) {
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryGreenButton(
                    text = "Beri Rating & Lihat Penghematan",
                    onClick = onRateClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (order.rating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(order.rating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = StarYellow, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.reviewComment.ifBlank { "Sudah diberi rating" },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
        }
    }
}

/**
 * Layar 2: Detail Pesanan
 */
@Composable
fun OrderDetailScreen(
    order: OrderEntity,
    onBack: () -> Unit,
    onContactStore: () -> Unit,
    onOpenPickupStatus: () -> Unit,
    onSimulateAutoCancel: () -> Unit
) {
    val context = LocalContext.current
    val statusEnum = runCatching { OrderStatus.valueOf(order.status) }.getOrDefault(OrderStatus.BELUM_DIAMBIL)

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Detail Pesanan ${order.id}",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (statusEnum != OrderStatus.BATAL) {
                    QrCodeCard(code = order.id, sizeDp = 200.dp)
                } else {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = LiveRedSoft,
                        border = BorderStroke(1.dp, LiveRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pesanan Dibatalkan",
                                style = MaterialTheme.typography.titleMedium,
                                color = LiveRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dana ${formatRupiah(order.totalPaid)} sudah dikembalikan ke Saldo LUDES.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBlack,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Store + Pickup Slot Info
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
                            text = order.productTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextBlack
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AccessTime, contentDescription = null, tint = ActionOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jam Ambil: ${order.pickupSlot}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextBlack
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = FreshGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${order.storeName} • ${order.storeAddress} (${order.distanceText})",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextBlack
                            )
                        }
                        Text(
                            text = "Dibayar lewat ${order.paymentMethod} • Total ${formatRupiah(order.totalPaid)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }

                // Hubungi Toko + Lihat Rute ke Toko (Buka Google Maps)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onContactStore,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, FreshGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FreshGreenDark),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp)
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hubungi Toko", style = MaterialTheme.typography.labelLarge)
                    }

                    SecondaryGreenButton(
                        text = "Lihat Rute ke Toko",
                        onClick = {
                            val query = Uri.encode("${order.storeName}, ${order.storeAddress}, Bandung")
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
                            try {
                                context.startActivity(mapIntent)
                            } catch (_: Exception) {
                                val webIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://maps.google.com/?q=$query")
                                )
                                runCatching { context.startActivity(webIntent) }
                            }
                        },
                        testTag = "open_maps_route_button",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (statusEnum == OrderStatus.BELUM_DIAMBIL) {
                    PrimaryOrangeButton(
                        text = "Status Pengambilan di Toko (3 Langkah)",
                        onClick = onOpenPickupStatus,
                        testTag = "open_pickup_status_button",
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = onSimulateAutoCancel) {
                        Text(
                            text = "Simulasi Lewat 15 Menit (Tes Batal Otomatis & Refund)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

/**
 * Layar 3: Status Pengambilan di Toko (Instruksi 3 Langkah + Konfirmasi)
 */
@Composable
fun OrderPickupStatusScreen(
    order: OrderEntity,
    onBack: () -> Unit,
    onAdvanceReady: () -> Unit,
    onConfirmPickedUp: () -> Unit
) {
    val steps = listOf(
        "1. Datang ke toko sesuai jam (${order.pickupSlot})",
        "2. Tunjukkan QR ke penjual (${order.id})",
        "3. Penjual scan QR, Pesanan selesai"
    )

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Status Pengambilan di Toko",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = BgWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!order.isReadyForPickup) {
                        SecondaryGreenButton(
                            text = "Tandai Siap Diambil",
                            onClick = onAdvanceReady,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PrimaryOrangeButton(
                        text = "Konfirmasi QR Sudah Di-Scan Penjual",
                        onClick = onConfirmPickedUp,
                        testTag = "confirm_pickup_done_button",
                        modifier = Modifier.fillMaxWidth()
                    )
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
                ThreeStageStatusCard(
                    stageIndex = if (order.isReadyForPickup) 1 else 0
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Instruksi 3 Langkah Pengambilan",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextBlack
                        )
                        steps.forEachIndexed { idx, stepText ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (idx < 2) FreshGreen else ActionOrangeSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (idx < 2) Icons.Filled.CheckCircle else Icons.Filled.QrCodeScanner,
                                        contentDescription = null,
                                        tint = if (idx < 2) Color.White else ActionOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextBlack
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    QrCodeCard(code = order.id, sizeDp = 170.dp)
                }
            }
        }
    }
}

/**
 * Layar 4: Konfirmasi Selesai
 */
@Composable
fun OrderCompletedScreen(
    order: OrderEntity,
    onGoToRating: () -> Unit,
    onBackToOrders: () -> Unit
) {
    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Pengambilan Selesai",
                onBack = onBackToOrders
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = FreshGreen,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Selesai",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Text(
                    text = "Yeay! Makanan sudah kamu ambil",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Terima kasih sudah menyelamatkan makanan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FreshGreenDark,
                    textAlign = TextAlign.Center
                )

                FoodProductImage(
                    imageKey = order.imageKey,
                    contentDescription = order.productTitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(20.dp))
                )

                ThreeStageStatusCard(stageIndex = 2)

                PrimaryOrangeButton(
                    text = "Beri Rating & Ulasan",
                    onClick = onGoToRating,
                    testTag = "go_to_rating_button",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Layar 5: Rating
 */
@Composable
fun OrderRatingScreen(
    order: OrderEntity,
    onBack: () -> Unit,
    onSubmitRating: (Int, String) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(if (order.rating > 0) order.rating else 5) }
    var comment by remember { mutableStateOf(order.reviewComment) }
    val savedRp = (order.originalPrice - order.bagPrice).coerceAtLeast(15000)

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Beri Rating Warung",
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
                // Green Savings Card: "Kamu Hemat 15rb"
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = FreshGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Kamu Hemat ${formatRupiahShort(savedRp)}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Dari ${order.storeName} • Selamatkan makanan hari ini",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }

                // Star Selector 1-5
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bagaimana rasa & kesegaran makanannya?",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { selectedStars = star },
                                    modifier = Modifier
                                        .size(52.dp)
                                        .testTag("rating_star_$star")
                                ) {
                                    Icon(
                                        imageVector = if (star <= selectedStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = "$star bintang",
                                        tint = if (star <= selectedStars) StarYellow else TextGray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Allowed typing box for rating comment
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            placeholder = {
                                Text(
                                    text = "enak banget, roti masih fresh!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextGray
                                )
                            },
                            label = { Text("Tulis ulasan kamu") },
                            minLines = 3,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FreshGreen,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rating_comment_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryOrangeButton(
                            text = "Kirim Rating",
                            onClick = {
                                val finalComment = comment.ifBlank { "enak banget, roti masih fresh!" }
                                onSubmitRating(selectedStars, finalComment)
                            },
                            testTag = "submit_rating_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
