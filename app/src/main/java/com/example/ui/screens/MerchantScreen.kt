package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.FoodCategory
import com.example.data.OrderEntity
import com.example.data.OrderStatus
import com.example.data.formatRupiah
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodProductImage
import com.example.ui.components.PickupOnlyBanner
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
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import java.io.File
import java.io.FileOutputStream

/**
 * Halaman Penjual Warung - Versi Ambil Sendiri Saja (Super Simple, Tanpa Ketik)
 */
@Composable
fun MerchantScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onPublishListing: (FoodCategory, Int, Int, String, String?) -> Unit,
    onAcceptOrder: (String) -> Unit,
    onRejectOrder: (String) -> Unit,
    onConfirmQrScanComplete: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Tayangkan Kantong, 1 = Pesanan Masuk

    var selectedCategory by remember { mutableStateOf(FoodCategory.NASI) }
    var selectedPrice by remember { mutableIntStateOf(10000) }
    var selectedStock by remember { mutableIntStateOf(3) }
    var selectedWindow by remember { mutableStateOf("18.00-20.00") }
    var capturedPhotoUri by remember { mutableStateOf<String?>(null) }
    var qrScanOrderToConfirm by remember { mutableStateOf<OrderEntity?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToCacheUri(context, bitmap)
            capturedPhotoUri = uri?.toString()
            onShowMessage("Foto sisa makanan berhasil diambil!")
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePictureLauncher.launch(null)
        } else {
            onShowMessage("Izin kamera ditolak. Ibu bisa pilih foto dari galeri.")
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            capturedPhotoUri = uri.toString()
            onShowMessage("Foto makanan berhasil dipilih!")
        }
    }

    val incomingOrders = uiState.orders.filter { it.status == OrderStatus.BELUM_DIAMBIL.name }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Penjual Warung (Ambil Sendiri)",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Big 2-Tab Switcher
                Surface(
                    shape = RoundedCornerShape(50),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf("Tayangkan Kantong", "Pesanan Masuk (${incomingOrders.size})").forEachIndexed { idx, label ->
                            val isSelected = selectedTab == idx
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) ActionOrange else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .clip(RoundedCornerShape(50))
                                    .clickable { selectedTab = idx }
                                    .testTag(if (idx == 0) "merchant_tab_publish" else "merchant_tab_orders")
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

                if (selectedTab == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PickupOnlyBanner(
                            title = "Khusus Ambil Sendiri di Warung",
                            subtitle = "Tidak perlu kirim kurir. Pembeli datang & tunjukkan QR ke Ibu."
                        )

                        // Tombol Besar 1: FOTO SISA MAKANAN (Buka Kamera)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "1. FOTO SISA MAKANAN",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                if (capturedPhotoUri != null) {
                                    FoodProductImage(
                                        imageKey = "nasi",
                                        customImageUri = capturedPhotoUri,
                                        contentDescription = "Foto sisa makanan warung",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f)
                                            .clip(RoundedCornerShape(14.dp))
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                SecondaryGreenButton(
                                    text = "FOTO SISA MAKANAN (Buka Kamera)",
                                    onClick = {
                                        val granted = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (granted) {
                                            takePictureLauncher.launch(null)
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    testTag = "merchant_open_camera_button",
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, SurfaceBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = TextBlack)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pilih Foto dari Galeri", color = TextBlack)
                                }
                            }
                        }

                        // Kategori Cepat (Tanpa Ketik)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Jenis Makanan",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(FoodCategory.entries) { cat ->
                                        val isSelected = selectedCategory == cat
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (isSelected) FreshGreen else BgLightGray,
                                            border = BorderStroke(1.dp, if (isSelected) FreshGreenDark else SurfaceBorder),
                                            modifier = Modifier
                                                .heightIn(min = 44.dp)
                                                .clip(RoundedCornerShape(50))
                                                .clickable { selectedCategory = cat }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = cat.label,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = if (isSelected) Color.White else TextBlack
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Tombol Besar 2: TENTUKAN HARGA (Rp10.000 / Rp15.000 / Rp20.000 - Tanpa Ketik)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "2. TENTUKAN HARGA (Tanpa Ketik)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(10000, 15000, 20000).forEach { price ->
                                        val isSelected = selectedPrice == price
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isSelected) ActionOrangeSoft else BgLightGray,
                                            border = BorderStroke(
                                                if (isSelected) 2.dp else 1.dp,
                                                if (isSelected) ActionOrange else SurfaceBorder
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(min = 52.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { selectedPrice = price }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = formatRupiah(price),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = if (isSelected) ActionOrangeDark else TextBlack
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Jumlah Kantong (Pilih 1-10)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Jumlah Kantong (Pilih 1-10)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items((1..10).toList()) { num ->
                                        val isSelected = selectedStock == num
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) FreshGreen else BgLightGray,
                                            border = BorderStroke(1.dp, if (isSelected) FreshGreenDark else SurfaceBorder),
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .clickable { selectedStock = num }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "$num",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = if (isSelected) Color.White else TextBlack
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Jam Ambil (Pilih 18.00-20.00)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Jam Ambil di Warung",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("18.00-20.00", "19.00-21.00", "20.00-22.00").forEach { window ->
                                        val isSelected = selectedWindow == window
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSelected) ActionOrangeSoft else BgLightGray,
                                            border = BorderStroke(1.dp, if (isSelected) ActionOrange else SurfaceBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(min = 48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { selectedWindow = window }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = window,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = if (isSelected) ActionOrangeDark else TextBlack
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PrimaryOrangeButton(
                            text = "TAYANGKAN - 30 detik langsung tayang",
                            onClick = {
                                onPublishListing(
                                    selectedCategory,
                                    selectedPrice,
                                    selectedStock,
                                    selectedWindow,
                                    capturedPhotoUri
                                )
                            },
                            testTag = "merchant_publish_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    // Layar Pesanan Masuk
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FreshGreenSoft,
                                border = BorderStroke(1.dp, FreshGreenBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Filled.Info, contentDescription = null, tint = FreshGreenDark)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tidak ada pengiriman kurir. Hanya boleh serahkan kantong jika QR pembeli sudah di-scan. Dana masuk H+1 setelah dipotong fee 10%.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextBlack
                                    )
                                }
                            }
                        }

                        if (incomingOrders.isEmpty()) {
                            item {
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
                                        Icon(Icons.Filled.Storefront, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Belum ada pesanan masuk baru", style = MaterialTheme.typography.titleSmall)
                                    }
                                }
                            }
                        } else {
                            items(incomingOrders, key = { it.id }) { order ->
                                val netToWarung = (order.bagPrice * 0.9).toInt()
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = BgWhite),
                                    border = BorderStroke(1.dp, SurfaceBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Pembeli: ${order.buyerName}",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = TextBlack
                                            )
                                            Text(
                                                text = order.id,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = ActionOrange
                                            )
                                        }
                                        Text(
                                            text = "${order.productTitle} • Jam Ambil: ${order.pickupSlot}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextGray
                                        )
                                        Text(
                                            text = "Dana Warung H+1 (potong fee 10%): ${formatRupiah(netToWarung)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = FreshGreenDark
                                        )

                                        if (!order.merchantAccepted) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { onRejectOrder(order.id) },
                                                    shape = RoundedCornerShape(50),
                                                    border = BorderStroke(1.5.dp, LiveRed),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LiveRed),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .heightIn(min = 48.dp)
                                                ) {
                                                    Text("Tolak")
                                                }
                                                SecondaryGreenButton(
                                                    text = "Terima",
                                                    onClick = { onAcceptOrder(order.id) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }

                                        PrimaryOrangeButton(
                                            text = "Scan QR Pembeli untuk Konfirmasi Selesai",
                                            onClick = { qrScanOrderToConfirm = order },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    qrScanOrderToConfirm?.let { order ->
        AlertDialog(
            onDismissRequest = { qrScanOrderToConfirm = null },
            title = { Text("Scan QR Pembeli") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = FreshGreenDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pesanan ${order.id} - ${order.buyerName}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Text(
                        text = "Pastikan pembeli menunjukkan QR ${order.id} sebelum menyerahkan kantong makanan. Dana bersih ${formatRupiah((order.bagPrice * 0.9).toInt())} akan diteruskan H+1.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmQrScanComplete(order.id)
                        qrScanOrderToConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("QR Cocok • Selesai")
                }
            },
            dismissButton = {
                TextButton(onClick = { qrScanOrderToConfirm = null }) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }
}

/**
 * Layar Khusus Penjual: Scan QR Pesanan Pembeli.
 * Kalau sudah di-scan, pesanan langsung berubah menjadi Sudah Diambil (Selesai & Sukses).
 */
@Composable
fun MerchantQrScannerScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onConfirmQrScanComplete: (String) -> Unit,
    onViewOrders: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val pendingOrders = uiState.orders.filter { it.status == OrderStatus.BELUM_DIAMBIL.name }
    var lastScannedSuccessOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "qrLaser")
    val laserOffsetDp by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 164f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    val scanCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val targetOrder = pendingOrders.firstOrNull()
            if (targetOrder != null) {
                onConfirmQrScanComplete(targetOrder.id)
                lastScannedSuccessOrder = targetOrder.copy(status = OrderStatus.SUDAH_DIAMBIL.name)
            } else {
                onShowMessage("QR terbaca, namun tidak ada pesanan aktif yang menunggu diambil.")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scanCameraLauncher.launch(null)
        } else {
            onShowMessage("Izin kamera diperlukan untuk scan QR, atau pilih pesanan di bawah.")
        }
    }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Penjual • Scan QR Pesanan",
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
                val successOrder = lastScannedSuccessOrder
                if (successOrder != null) {
                    // Tampilan Sukses Setelah QR Di-Scan
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = BgWhite,
                        border = BorderStroke(2.dp, FreshGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = FreshGreen,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Sukses",
                                        tint = Color.White,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }
                            }

                            Text(
                                text = "QR Berhasil Di-Scan!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = FreshGreenDark
                            )
                            Text(
                                text = "Pesanan ${successOrder.id} Selesai & Sukses",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextBlack
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FreshGreenSoft,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Pembeli: ${successOrder.buyerName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextBlack
                                    )
                                    Text(
                                        text = "${successOrder.productTitle} • Jam ${successOrder.pickupSlot}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextBlack
                                    )
                                    Text(
                                        text = "Status: Sudah Diambil (Selesai)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = FreshGreenDark
                                    )
                                    Text(
                                        text = "Dana warung ${formatRupiah((successOrder.bagPrice * 0.9).toInt())} (potong fee 10%) diteruskan H+1.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FreshGreenDark
                                    )
                                }
                            }

                            PrimaryOrangeButton(
                                text = "Scan QR Pesanan Lain",
                                onClick = { lastScannedSuccessOrder = null },
                                modifier = Modifier.fillMaxWidth()
                            )
                            SecondaryGreenButton(
                                text = "Lihat Status di Pesanan",
                                onClick = onViewOrders,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // Viewfinder Kamera Scan QR
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(2.5.dp, FreshGreen, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = "Bingkai Scan QR",
                                    tint = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .size(110.dp)
                                        .align(Alignment.Center)
                                )
                                Box(
                                    modifier = Modifier
                                        .offset(y = laserOffsetDp.dp)
                                        .fillMaxWidth(0.84f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(FreshGreen)
                                )
                            }

                            Text(
                                text = "Arahkan kamera ke QR Code pada HP pembeli",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )

                            PrimaryOrangeButton(
                                text = "Buka Kamera Scan QR",
                                onClick = {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) {
                                        scanCameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                testTag = "merchant_open_qr_camera_button",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Daftar Pesanan Aktif Siap Scan QR (Tanpa Ketik)
                    Text(
                        text = "Pesanan Menunggu Scan QR (${pendingOrders.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextBlack
                    )

                    if (pendingOrders.isEmpty()) {
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
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = FreshGreen,
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Semua pesanan sudah selesai diambil!",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextBlack
                                )
                            }
                        }
                    } else {
                        pendingOrders.forEach { order ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BgWhite),
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
                                            text = "Kode QR: ${order.id}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = ActionOrange
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = ActionOrangeSoft
                                        ) {
                                            Text(
                                                text = "Belum Diambil",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = ActionOrangeDark,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${order.buyerName} • ${order.productTitle}",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextBlack
                                    )
                                    Text(
                                        text = "Jam Ambil: ${order.pickupSlot} • Dana Warung: ${formatRupiah((order.bagPrice * 0.9).toInt())}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGray
                                    )

                                    SecondaryGreenButton(
                                        text = "Scan & Selesaikan Pesanan ${order.id}",
                                        onClick = {
                                            onConfirmQrScanComplete(order.id)
                                            lastScannedSuccessOrder = order.copy(status = OrderStatus.SUDAH_DIAMBIL.name)
                                        },
                                        testTag = "confirm_scan_qr_${order.id}",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun saveBitmapToCacheUri(context: Context, bitmap: Bitmap): Uri? {
    return runCatching {
        val file = File(context.cacheDir, "warung_food_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Uri.fromFile(file)
    }.getOrNull()
}
