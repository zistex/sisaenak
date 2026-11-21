package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ProductEntity
import com.example.data.formatCompactCount
import com.example.data.formatRupiah
import com.example.data.resolveFoodDrawable
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.ActionOrangeDark
import com.example.ui.theme.ActionOrangeSoft
import com.example.ui.theme.BgWhite
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenBorder
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenSoft
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StarYellow
import com.example.ui.theme.StatusAmberSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import kotlin.math.abs

enum class MainTab(val route: String, val label: String) {
    BERANDA("beranda", "Beranda"),
    PESANAN("pesanan", "Pesanan"),
    LIVE("live", "Live"),
    AKUN("akun", "Akun")
}

@Composable
fun SisaEnakTopHeader(
    location: String,
    onLocationClick: () -> Unit,
    onBellClick: () -> Unit,
    unreadNotificationCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BgWhite,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo LUDES
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("header_logo")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FreshGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = "Logo LUDES",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LUDES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = TextBlack
                    )
                    Text(
                        text = "Sebelum Ludes • Ambil Sendiri",
                        style = MaterialTheme.typography.labelMedium,
                        color = FreshGreenDark
                    )
                }
            }

            // Location pill + Notification bell
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = FreshGreenSoft,
                    border = BorderStroke(1.dp, FreshGreenBorder),
                    modifier = Modifier
                        .testTag("location_chip")
                        .clip(RoundedCornerShape(50))
                        .clickable { onLocationClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Lokasi",
                            tint = FreshGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Ganti lokasi",
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onBellClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("bell_button")
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifikasi",
                            tint = TextBlack,
                            modifier = Modifier.size(24.dp)
                        )
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(ActionOrange)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubScreenTopBar(
    title: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        color = BgWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextBlack
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = TextBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (trailing != null) {
                trailing()
            }
        }
    }
}

/**
 * Mandatory Bottom Navigation Bar: EXACTLY 4 tabs (Beranda | Pesanan | Live | Akun).
 * Live is at position 3 with a pulsing red dot when any warung is Live.
 */
@Composable
fun SisaEnakBottomBar(
    currentRoute: String,
    hasLiveNow: Boolean,
    activeOrderCount: Int,
    onSelectTab: (MainTab) -> Unit,
    isDarkBar: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveDot")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val barContainer = BgWhite
    val inactiveTint = TextGray
    val indicatorTint = ActionOrangeSoft
    val dotBorder = BgWhite

    NavigationBar(
        containerColor = barContainer,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        MainTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            val selectedIcon = when (tab) {
                MainTab.BERANDA -> Icons.Filled.Home
                MainTab.PESANAN -> Icons.Filled.ReceiptLong
                MainTab.LIVE -> Icons.Filled.LiveTv
                MainTab.AKUN -> Icons.Filled.Person
            }
            val unselectedIcon = when (tab) {
                MainTab.BERANDA -> Icons.Outlined.Home
                MainTab.PESANAN -> Icons.Outlined.ReceiptLong
                MainTab.LIVE -> Icons.Outlined.LiveTv
                MainTab.AKUN -> Icons.Outlined.Person
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                modifier = Modifier.testTag("nav_${tab.route}"),
                icon = {
                    Box {
                        Icon(
                            imageVector = if (selected) selectedIcon else unselectedIcon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(24.dp)
                        )
                        if (tab == MainTab.LIVE && hasLiveNow) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(LiveRed.copy(alpha = pulseAlpha))
                                    .border(1.5.dp, dotBorder, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                        if (tab == MainTab.PESANAN && activeOrderCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(ActionOrange)
                                    .border(1.5.dp, dotBorder, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ActionOrange,
                    selectedTextColor = ActionOrange,
                    indicatorColor = indicatorTint,
                    unselectedIconColor = inactiveTint,
                    unselectedTextColor = inactiveTint
                )
            )
        }
    }
}

@Composable
fun FoodProductImage(
    imageKey: String,
    customImageUri: String? = null,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (!customImageUri.isNullOrBlank()) {
        AsyncImage(
            model = Uri.parse(customImageUri),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = resolveFoodDrawable(imageKey)),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Composable
fun PickupOnlyBanner(
    modifier: Modifier = Modifier,
    title: String = "AMBIL DI TOKO - GRATIS",
    subtitle: String = "Semua pesanan hanya ambil sendiri di warung. Tanpa ongkir & tanpa kurir."
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = FreshGreenSoft,
        border = BorderStroke(1.dp, FreshGreenBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FreshGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = "Ambil di toko",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = FreshGreenDark
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBlack
                )
            }
        }
    }
}

@Composable
fun PrimaryOrangeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = ActionOrange,
            contentColor = Color.White,
            disabledContainerColor = SurfaceBorder,
            disabledContentColor = TextGray
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier
            .heightIn(min = 50.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SecondaryGreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = FreshGreen,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        modifier = modifier
            .heightIn(min = 48.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/**
 * Uniform Food Card Format:
 * Foto Makanan, Nama Warung, Jarak (0.8 km), Jam Ambil (19.00-21.00),
 * Harga Coret (Rp25.000), Harga Murah (Rp10.000), Sisa Stok (Sisa 3),
 * Tombol BELI Oranye + Ikon Hati untuk Favorit di pojok foto.
 */
@Composable
fun FoodBagCard(
    product: ProductEntity,
    onCardClick: () -> Unit,
    onBuyClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgWhite),
        border = BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("food_card_${product.id}")
            .clickable { onCardClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9.5f)
            ) {
                FoodProductImage(
                    imageKey = product.imageKey,
                    customImageUri = product.customImageUri,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize()
                )

                // Top-left stock & live badges
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (product.stock > 0) ActionOrange else TextGray
                    ) {
                        Text(
                            text = if (product.stock > 0) "Sisa ${product.stock}" else "Habis",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (product.isLive) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = LiveRed
                        ) {
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Heart Favorite icon at top-right corner of photo
                Surface(
                    shape = CircleShape,
                    color = BgWhite.copy(alpha = 0.92f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(44.dp)
                        .testTag("favorite_heart_${product.id}")
                        .clickable { onToggleFavorite() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (product.isFavorite) LiveRed else TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Bottom-left Ambil Sendiri badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = FreshGreen,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ambil Sendiri • Gratis",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.storeName,
                        style = MaterialTheme.typography.labelLarge,
                        color = FreshGreenDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = StatusAmberSoft
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = StarYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f", product.rating),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextBlack
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = product.distanceText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Ambil ${product.pickupWindow}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Statistik View & Favorite Produk
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Jumlah dilihat",
                            tint = TextGray,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatCompactCount(product.viewCount)} dilihat",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Jumlah favorit",
                            tint = LiveRed.copy(alpha = 0.85f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatCompactCount(product.favoriteCount)} favorit",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = formatRupiah(product.originalPrice),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = TextGray
                        )
                        Text(
                            text = formatRupiah(product.discountPrice),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = ActionOrange
                        )
                    }

                    Button(
                        onClick = onBuyClick,
                        enabled = product.stock > 0,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ActionOrange,
                            contentColor = Color.White,
                            disabledContainerColor = SurfaceBorder,
                            disabledContentColor = TextGray
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("buy_button_${product.id}")
                    ) {
                        Text(
                            text = if (product.stock > 0) "BELI" else "HABIS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Deterministic crisp QR Code Matrix rendered on Canvas for instant offline-safe ticket display.
 */
@Composable
fun QrCodeCard(
    code: String,
    sizeDp: Dp = 190.dp,
    caption: String = "Tunjukkan QR ini ke penjual saat ambil"
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BgWhite,
        border = BorderStroke(1.5.dp, SurfaceBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(
                modifier = Modifier
                    .size(sizeDp)
                    .background(Color.White)
            ) {
                val grid = 21
                val cell = size.width / grid

                fun drawFinder(startCol: Int, startRow: Int) {
                    // 7x7 outer black
                    drawRect(
                        color = Color(0xFF111827),
                        topLeft = Offset(startCol * cell, startRow * cell),
                        size = Size(7 * cell, 7 * cell)
                    )
                    // 5x5 white
                    drawRect(
                        color = Color.White,
                        topLeft = Offset((startCol + 1) * cell, (startRow + 1) * cell),
                        size = Size(5 * cell, 5 * cell)
                    )
                    // 3x3 inner black
                    drawRect(
                        color = Color(0xFF111827),
                        topLeft = Offset((startCol + 2) * cell, (startRow + 2) * cell),
                        size = Size(3 * cell, 3 * cell)
                    )
                }

                fun isFinderZone(r: Int, c: Int): Boolean {
                    val topLeft = r < 8 && c < 8
                    val topRight = r < 8 && c >= grid - 8
                    val bottomLeft = r >= grid - 8 && c < 8
                    return topLeft || topRight || bottomLeft
                }

                val seed = code.hashCode()
                for (r in 0 until grid) {
                    for (c in 0 until grid) {
                        if (isFinderZone(r, c)) continue
                        val isTiming = (r == 6 || c == 6) && ((r + c) % 2 == 0)
                        val bitHash = abs((seed * 31 + r * 17 + c * 13) xor (r * c + 7))
                        val isModule = isTiming || (bitHash % 11 < 6)
                        if (isModule) {
                            drawRect(
                                color = Color(0xFF111827),
                                topLeft = Offset(c * cell, r * cell),
                                size = Size(cell + 0.4f, cell + 0.4f)
                            )
                        }
                    }
                }

                drawFinder(0, 0)
                drawFinder(grid - 7, 0)
                drawFinder(0, grid - 7)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}
