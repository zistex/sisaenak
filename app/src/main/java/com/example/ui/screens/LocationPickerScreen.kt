package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.bandungAreaPresets
import com.example.data.haversineDistanceKm
import com.example.data.nearestBandungAreaLabel
import com.example.data.storeCoordinatesForProduct
import com.example.ui.SisaEnakUiState
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.SubScreenTopBar
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.ActionOrangeBorder
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
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun LocationPickerScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onConfirmLocation: (label: String, lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var markerLat by remember { mutableDoubleStateOf(uiState.userPrefs.userLat) }
    var markerLng by remember { mutableDoubleStateOf(uiState.userPrefs.userLng) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var gpsStatusNote by remember { mutableStateOf<String?>(null) }

    val animLat by animateFloatAsState(
        targetValue = markerLat.toFloat(),
        animationSpec = tween(220),
        label = "animLat"
    )
    val animLng by animateFloatAsState(
        targetValue = markerLng.toFloat(),
        animationSpec = tween(220),
        label = "animLng"
    )
    val animZoom by animateFloatAsState(
        targetValue = zoomLevel,
        animationSpec = tween(220),
        label = "animZoom"
    )

    val detectedLabel = remember(markerLat, markerLng) {
        nearestBandungAreaLabel(markerLat, markerLng)
    }

    val storesWithCoords = remember(uiState.products) {
        uiState.products.map { p ->
            val (sLat, sLng) = storeCoordinatesForProduct(p.id, p.storeAddress)
            Triple(p, sLat, sLng)
        }.distinctBy { it.first.storeName }
    }

    val nearbyStoreCount = remember(storesWithCoords, markerLat, markerLng) {
        storesWithCoords.count { (_, sLat, sLng) ->
            haversineDistanceKm(markerLat, markerLng, sLat, sLng) <= 1.5
        }
    }

    fun applyDeviceGpsLocation() {
        val loc = readLastKnownLocation(context)
        if (loc != null) {
            val inBandungRange = loc.latitude in -7.15..-6.75 && loc.longitude in 107.45..107.80
            if (inBandungRange) {
                markerLat = loc.latitude
                markerLng = loc.longitude
                gpsStatusNote = "Lokasi GPS perangkat terdeteksi"
            } else {
                // Keep usability in Bandung area if emulator defaults to Mountain View
                markerLat = -6.8904
                markerLng = 107.6134
                gpsStatusNote = "GPS aktif • Dipusatkan ke area layanan Bandung (Dago)"
            }
        } else {
            markerLat = -6.8904
            markerLng = 107.6134
            gpsStatusNote = "Sinyal GPS belum tersedia • Geser peta untuk set marker"
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            applyDeviceGpsLocation()
        } else {
            gpsStatusNote = "Izin lokasi ditolak • Geser marker peta secara manual"
        }
    }

    fun requestCurrentGps() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            applyDeviceGpsLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pinPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgLightGray)
    ) {
        SubScreenTopBar(
            title = "Atur Titik Lokasi",
            onBack = onBack
        )

        // Interactive Map Viewport
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFEDF2F7))
        ) {
            val density = LocalDensity.current
            val widthPx = with(density) { maxWidth.toPx() }
            val heightPx = with(density) { maxHeight.toPx() }

            // Geographic scale: pixels per degree (centered around animLat, animLng)
            val basePxPerDeg = 16500f * animZoom
            val cosLat = 0.9927f // cos(-6.9 deg)

            fun latLngToScreen(lat: Double, lng: Double): Offset {
                val dx = ((lng - animLng).toFloat() * cosLat) * basePxPerDeg
                val dy = ((animLat - lat).toFloat()) * basePxPerDeg
                return Offset(widthPx / 2f + dx, heightPx / 2f + dy)
            }

            // Interactive Vector City Map Canvas (Pan + Tap to Reposition Marker)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(zoomLevel) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val dLng = -(dragAmount.x / (basePxPerDeg * cosLat)).toDouble()
                            val dLat = (dragAmount.y / basePxPerDeg).toDouble()
                            markerLat = (markerLat + dLat).coerceIn(-6.97, -6.84)
                            markerLng = (markerLng + dLng).coerceIn(107.56, 107.68)
                            gpsStatusNote = null
                        }
                    }
                    .pointerInput(zoomLevel) {
                        detectTapGestures { tapOffset ->
                            val dx = tapOffset.x - widthPx / 2f
                            val dy = tapOffset.y - heightPx / 2f
                            val dLng = (dx / (basePxPerDeg * cosLat)).toDouble()
                            val dLat = -(dy / basePxPerDeg).toDouble()
                            markerLat = (markerLat + dLat).coerceIn(-6.97, -6.84)
                            markerLng = (markerLng + dLng).coerceIn(107.56, 107.68)
                            gpsStatusNote = null
                        }
                    }
                    .testTag("location_map_canvas")
            ) {
                // 1. Cartographic base blocks
                drawRect(color = Color(0xFFEEF2F6))

                // Soft city blocks grid
                val gridStepLat = 0.004
                val gridStepLng = 0.004
                val startLat = -6.96
                val endLat = -6.84
                val startLng = 107.56
                val endLng = 107.68

                var gLat = startLat
                while (gLat <= endLat) {
                    val p1 = latLngToScreen(gLat, startLng)
                    val p2 = latLngToScreen(gLat, endLng)
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = p1,
                        end = p2,
                        strokeWidth = 2.dp.toPx()
                    )
                    gLat += gridStepLat
                }
                var gLng = startLng
                while (gLng <= endLng) {
                    val p1 = latLngToScreen(startLat, gLng)
                    val p2 = latLngToScreen(endLat, gLng)
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = p1,
                        end = p2,
                        strokeWidth = 2.dp.toPx()
                    )
                    gLng += gridStepLng
                }

                // 2. Green Park Zones (Babakan Siliwangi, Taman Lansia, Balai Kota)
                val parks = listOf(
                    Triple(-6.8845, 107.6065, Size(110.dp.toPx(), 70.dp.toPx())),
                    Triple(-6.9008, 107.6195, Size(90.dp.toPx(), 65.dp.toPx())),
                    Triple(-6.9125, 107.6095, Size(85.dp.toPx(), 60.dp.toPx()))
                )
                parks.forEach { (pLat, pLng, pSize) ->
                    val center = latLngToScreen(pLat, pLng)
                    drawRoundRect(
                        color = Color(0xFFD1FAE5),
                        topLeft = Offset(center.x - pSize.width / 2f, center.y - pSize.height / 2f),
                        size = pSize,
                        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                    )
                }

                // 3. Cikapundung River Ribbon
                val riverPoints = listOf(
                    latLngToScreen(-6.875, 107.606),
                    latLngToScreen(-6.890, 107.608),
                    latLngToScreen(-6.905, 107.607),
                    latLngToScreen(-6.925, 107.610),
                    latLngToScreen(-6.945, 107.613)
                )
                val riverPath = Path().apply {
                    moveTo(riverPoints.first().x, riverPoints.first().y)
                    for (i in 1 until riverPoints.size) {
                        lineTo(riverPoints[i].x, riverPoints[i].y)
                    }
                }
                drawPath(
                    path = riverPath,
                    color = Color(0xFFBFDBFE),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )

                // 4. Arterial Bandung Streets (casing + white inner road)
                val roads = listOf(
                    // Jl. Dago / Ir. H. Juanda
                    listOf(-6.875 to 107.614, -6.890 to 107.613, -6.906 to 107.611, -6.920 to 107.610),
                    // Jl. Dipatiukur -> Gazibu
                    listOf(-6.885 to 107.617, -6.894 to 107.6176, -6.901 to 107.6185),
                    // Jl. Riau / LLRE Martadinata
                    listOf(-6.906 to 107.602, -6.9062 to 107.6198, -6.9065 to 107.638),
                    // Jl. Cihampelas
                    listOf(-6.880 to 107.604, -6.8958 to 107.6038, -6.912 to 107.6045),
                    // Jl. Buah Batu
                    listOf(-6.916 to 107.621, -6.9338 to 107.6245, -6.950 to 107.627),
                    // Jl. Siliwangi / Cisitu
                    listOf(-6.884 to 107.598, -6.8845 to 107.613, -6.886 to 107.626)
                )
                roads.forEach { road ->
                    val path = Path().apply {
                        val first = latLngToScreen(road.first().first, road.first().second)
                        moveTo(first.x, first.y)
                        road.drop(1).forEach { (rLat, rLng) ->
                            val pt = latLngToScreen(rLat, rLng)
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFFCBD5E1),
                        style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 5. 1.5 km Reference Radius Ring centered on User Marker
                val centerPt = Offset(size.width / 2f, size.height / 2f)
                val radius15KmPx = (1.5f / 111.0f) * basePxPerDeg
                drawCircle(
                    color = FreshGreen.copy(alpha = 0.10f),
                    radius = radius15KmPx * pulseScale.coerceAtMost(1.03f),
                    center = centerPt
                )
                drawCircle(
                    color = FreshGreen.copy(alpha = 0.45f),
                    radius = radius15KmPx,
                    center = centerPt,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                    )
                )
            }

            // Street & Area Labels Overlay on Map
            bandungAreaPresets.forEach { preset ->
                val screenPos = latLngToScreen(preset.lat, preset.lng)
                if (screenPos.x in -40f..(widthPx + 40f) && screenPos.y in -40f..(heightPx + 40f)) {
                    val isNearCenter = haversineDistanceKm(markerLat, markerLng, preset.lat, preset.lng) < 0.45
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isNearCenter) ActionOrangeSoft.copy(alpha = 0.94f) else BgWhite.copy(alpha = 0.88f),
                        border = BorderStroke(0.8.dp, if (isNearCenter) ActionOrange else SurfaceBorder),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (screenPos.x - 38.dp.toPx()).roundToInt(),
                                    (screenPos.y + 16.dp.toPx()).roundToInt()
                                )
                            }
                    ) {
                        Text(
                            text = preset.shortName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isNearCenter) ActionOrangeDark else TextGray,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Store / Warung Pins Overlay
            storesWithCoords.forEach { (product, sLat, sLng) ->
                val screenPos = latLngToScreen(sLat, sLng)
                if (screenPos.x in -30f..(widthPx + 30f) && screenPos.y in -30f..(heightPx + 30f)) {
                    val inRadius = haversineDistanceKm(markerLat, markerLng, sLat, sLng) <= 1.5
                    Surface(
                        shape = CircleShape,
                        color = if (inRadius) FreshGreen else Color(0xFF94A3B8),
                        border = BorderStroke(1.5.dp, BgWhite),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (screenPos.x - 13.dp.toPx()).roundToInt(),
                                    (screenPos.y - 13.dp.toPx()).roundToInt()
                                )
                            }
                            .size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = product.storeName,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Center User Reference Marker Pin (Fixed at Screen Center)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = TextBlack.copy(alpha = 0.88f),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "Lokasi Kamu",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Box(contentAlignment = Alignment.Center) {
                    // Ground shadow ring
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 8.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(ActionOrange.copy(alpha = 0.28f))
                    )
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Pin Lokasi Pengguna",
                        tint = ActionOrange,
                        modifier = Modifier.size(44.dp)
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = (-5).dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Top Map Instruction + Radius Pill
            Surface(
                shape = RoundedCornerShape(50),
                color = BgWhite.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, SurfaceBorder),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.TouchApp,
                        contentDescription = null,
                        tint = ActionOrange,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = gpsStatusNote ?: "Geser peta atau tap area • $nearbyStoreCount warung dalam 1.5 km",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Map Controls: Zoom +/- & Use Current GPS
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    shadowElevation = 3.dp
                ) {
                    Column {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(2.0f) }
                                .testTag("map_zoom_in")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Perbesar peta",
                                tint = TextBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(1.dp)
                                .background(SurfaceBorder)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.65f) }
                                .testTag("map_zoom_out")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Perkecil peta",
                                tint = TextBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = BgWhite,
                    border = BorderStroke(1.dp, FreshGreenBorder),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { requestCurrentGps() }
                        .testTag("map_use_gps_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = "Gunakan GPS saat ini",
                            tint = FreshGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lokasi Saya",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = FreshGreenDark
                        )
                    }
                }
            }
        }

        // Bottom Professional Location Confirmation Sheet
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = BgWhite,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Area Chips
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Lompat Cepat ke Area",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextBlack
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(bandungAreaPresets) { preset ->
                                val isSelected = detectedLabel == preset.label
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = BgWhite,
                                    border = BorderStroke(
                                        width = if (isSelected) 1.dp else 0.8.dp,
                                        color = if (isSelected) ActionOrange else SurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .heightIn(min = 38.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            markerLat = preset.lat
                                            markerLng = preset.lng
                                            gpsStatusNote = null
                                        }
                                        .testTag("area_chip_${preset.shortName.lowercase()}")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = preset.shortName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) ActionOrangeDark else TextBlack
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Selected Marker Details Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = FreshGreenSoft,
                        border = BorderStroke(1.dp, FreshGreenBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(FreshGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = detectedLabel,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = TextBlack
                                )
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "Koordinat: %.4f, %.4f • $nearbyStoreCount warung terdekat",
                                        markerLat,
                                        markerLng
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FreshGreenDark
                                )
                            }
                        }
                    }

                    PrimaryOrangeButton(
                        text = "Simpan & Jadikan Acuan Lokasi",
                        onClick = { onConfirmLocation(detectedLabel, markerLat, markerLng) },
                        testTag = "confirm_map_location_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun readLastKnownLocation(context: Context): Location? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )
    var best: Location? = null
    for (p in providers) {
        val loc = runCatching { lm.getLastKnownLocation(p) }.getOrNull() ?: continue
        if (best == null || loc.accuracy < best.accuracy) {
            best = loc
        }
    }
    return best
}
