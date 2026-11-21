package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.OrderEntity
import com.example.data.PaymentMethod
import com.example.data.ProductEntity
import com.example.data.ProductReviewEntity
import com.example.data.formatCompactCount
import com.example.data.formatRupiah
import com.example.data.resolveProductGalleryKeys
import kotlinx.coroutines.launch
import com.example.ui.components.FoodProductImage
import com.example.ui.components.PickupOnlyBanner
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.QrCodeCard
import com.example.ui.components.SecondaryGreenButton
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
import com.example.ui.theme.LiveRed
import com.example.ui.theme.StarYellow
import com.example.ui.theme.StatusAmberSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    reviews: List<ProductReviewEntity>,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onBuyNow: () -> Unit
) {
    val discountPct = ((product.originalPrice - product.discountPrice) * 100) /
        product.originalPrice.coerceAtLeast(1)
    val galleryItems = remember(product.id, product.imageKey, product.customImageUri, product.category) {
        resolveProductGalleryKeys(product.imageKey, product.customImageUri, product.category)
    }
    val pagerState = rememberPagerState(pageCount = { galleryItems.size })
    val scope = rememberCoroutineScope()
    var fullscreenPhotoIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        containerColor = BgLightGray,
        bottomBar = {
            Surface(
                color = BgWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Harga Hemat",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                        Text(
                            text = formatRupiah(product.discountPrice),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = ActionOrange
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    PrimaryOrangeButton(
                        text = if (product.stock > 0) "BELI SEKARANG" else "STOK HABIS",
                        enabled = product.stock > 0,
                        onClick = onBuyNow,
                        testTag = "detail_buy_now_button",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.widthIn(max = 600.dp)) {
                // Big Swipeable 3-Photo Food Gallery (Tap to open Fullscreen Swipeable Lightbox)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("product_detail_photo_pager")
                    ) { page ->
                        val item = galleryItems[page]
                        val isUri = item.startsWith("uri:")
                        FoodProductImage(
                            imageKey = if (isUri) product.imageKey else item,
                            customImageUri = if (isUri) item.removePrefix("uri:") else null,
                            contentDescription = "${product.title} foto ${page + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { fullscreenPhotoIndex = page }
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = BgWhite.copy(alpha = 0.92f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 40.dp, start = 14.dp)
                            .size(44.dp)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextBlack
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = BgWhite.copy(alpha = 0.92f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 14.dp)
                            .size(44.dp)
                            .clickable { onToggleFavorite() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (product.isFavorite) LiveRed else TextGray
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ActionOrange,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Sisa ${product.stock} Kantong • Hemat $discountPct%",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Photo Counter & Tap-to-Zoom Pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp)
                            .clickable { fullscreenPhotoIndex = pagerState.currentPage }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = "Galeri foto",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${pagerState.currentPage + 1}/${galleryItems.size} • Perbesar",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // 3-Photo Thumbnail Strip (Tap to switch, or tap main image to swipe fullscreen)
                Surface(
                    color = BgWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        galleryItems.forEachIndexed { idx, item ->
                            val isSelected = pagerState.currentPage == idx
                            val isUri = item.startsWith("uri:")
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ActionOrange else SurfaceBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (isSelected) {
                                            fullscreenPhotoIndex = idx
                                        } else {
                                            scope.launch { pagerState.animateScrollToPage(idx) }
                                        }
                                    }
                                    .testTag("product_thumb_$idx")
                            ) {
                                FoodProductImage(
                                    imageKey = if (isUri) product.imageKey else item,
                                    customImageUri = if (isUri) item.removePrefix("uri:") else null,
                                    contentDescription = "Thumbnail foto ${idx + 1}",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { fullscreenPhotoIndex = pagerState.currentPage }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ZoomIn,
                                contentDescription = null,
                                tint = FreshGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lihat Foto",
                                style = MaterialTheme.typography.labelMedium,
                                color = FreshGreenDark
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Product Title & Rating
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = FreshGreenSoft
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Rating",
                                            tint = StarYellow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${product.rating} • Terlaris",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = FreshGreenDark
                                        )
                                    }
                                }

                                Text(
                                    text = "Ambil ${product.pickupWindow}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextGray
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = TextBlack
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatRupiah(product.discountPrice),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = ActionOrange
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = formatRupiah(product.originalPrice),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = TextGray
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Statistik Produk: Rating, Dilihat, Favorit
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BgLightGray,
                                border = BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Rating",
                                            tint = StarYellow,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${product.rating} Rating",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextBlack
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "Dilihat",
                                            tint = FreshGreenDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${formatCompactCount(product.viewCount)} Dilihat",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextBlack
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Favorit",
                                            tint = LiveRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${formatCompactCount(product.favoriteCount)} Favorit",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextBlack
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = SurfaceBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Isi Kantong Kejutan",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextGray
                            )
                        }
                    }

                    // Alamat Toko Card (Hanya Ambil Sendiri)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Lokasi Pengambilan (Ambil Sendiri)",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Alamat toko",
                                    tint = FreshGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${product.storeName} - ${product.storeAddress} - ${product.distanceText} dari kamu",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = TextBlack
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            PickupOnlyBanner()
                        }
                    }

                    // Komentar & Ulasan Pembeli
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_reviews_section")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Komentar & Ulasan Pembeli",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextBlack
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = FreshGreenSoft
                                ) {
                                    Text(
                                        text = "${reviews.size} ulasan",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = FreshGreenDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (reviews.isEmpty()) {
                                Text(
                                    text = "Belum ada komentar ulasan untuk produk ini. Yuk pesan dan bagikan pengalamanmu!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextGray
                                )
                            } else {
                                reviews.forEachIndexed { index, review ->
                                    if (index > 0) {
                                        HorizontalDivider(color = SurfaceBorder)
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = FreshGreenSoft,
                                                    modifier = Modifier.size(34.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = review.reviewerName.firstOrNull()?.uppercase() ?: "S",
                                                            style = MaterialTheme.typography.labelLarge,
                                                            color = FreshGreenDark
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = review.reviewerName,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = TextBlack
                                                    )
                                                    Text(
                                                        text = review.timeLabel,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextGray
                                                    )
                                                }
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                repeat(5) { starIdx ->
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = null,
                                                        tint = if (starIdx < review.rating) StarYellow else SurfaceBorder,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = review.comment,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextBlack
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

    fullscreenPhotoIndex?.let { startIndex ->
        FullscreenPhotoGalleryDialog(
            product = product,
            galleryItems = galleryItems,
            initialIndex = startIndex,
            onDismiss = { fullscreenPhotoIndex = null }
        )
    }
}

@Composable
private fun FullscreenPhotoGalleryDialog(
    product: ProductEntity,
    galleryItems: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val modalPagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (galleryItems.size - 1).coerceAtLeast(0)),
        pageCount = { galleryItems.size }
    )
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Swipeable Fullscreen Pager with Double-Tap Zoom
            HorizontalPager(
                state = modalPagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fullscreen_photo_pager")
            ) { page ->
                var zoomed by remember(page) { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (zoomed) 2f else 1f,
                    label = "photoZoom"
                )
                val item = galleryItems[page]
                val isUri = item.startsWith("uri:")

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(page) {
                            detectTapGestures(
                                onDoubleTap = { zoomed = !zoomed }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    FoodProductImage(
                        imageKey = if (isUri) product.imageKey else item,
                        customImageUri = if (isUri) item.removePrefix("uri:") else null,
                        contentDescription = "${product.title} foto ${page + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                    )
                }
            }

            // Top Bar: Counter + Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.16f)
                ) {
                    Text(
                        text = "Foto ${modalPagerState.currentPage + 1} dari ${galleryItems.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onDismiss() }
                        .testTag("fullscreen_gallery_close")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Tutup galeri foto",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Caption + Thumbnails + Swipe Hint
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.68f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    text = "Geser kiri/kanan untuk lihat foto lain • Ketuk 2x untuk zoom",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    galleryItems.forEachIndexed { idx, item ->
                        val isSelected = modalPagerState.currentPage == idx
                        val isUri = item.startsWith("uri:")
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ActionOrange else Color.White.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    scope.launch { modalPagerState.animateScrollToPage(idx) }
                                }
                        ) {
                            FoodProductImage(
                                imageKey = if (isUri) product.imageKey else item,
                                customImageUri = if (isUri) item.removePrefix("uri:") else null,
                                contentDescription = "Thumbnail ${idx + 1}",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutPickupScreen(
    product: ProductEntity,
    selectedSlot: String,
    onSelectSlot: (String) -> Unit,
    onBack: () -> Unit,
    onProceedToPayment: () -> Unit
) {
    val slots = product.pickupSlotsCsv
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .ifEmpty { listOf("19.00 - 19.30", "19.30 - 20.00", "20.00 - 20.30") }

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "AMBIL DI TOKO - GRATIS",
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
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    PrimaryOrangeButton(
                        text = "LANJUT KE PEMBAYARAN",
                        enabled = selectedSlot.isNotBlank(),
                        onClick = onProceedToPayment,
                        testTag = "checkout_to_payment_button",
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
                PickupOnlyBanner(
                    title = "AMBIL DI TOKO - GRATIS",
                    subtitle = "Tidak ada pilihan kurir, tidak ada ongkir, dan tidak ada alamat pengiriman."
                )

                // Store Summary
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FoodProductImage(
                            imageKey = product.imageKey,
                            customImageUri = product.customImageUri,
                            contentDescription = product.title,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.storeName,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack
                            )
                            Text(
                                text = "${product.storeAddress} • ${product.distanceText}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatRupiah(product.discountPrice),
                                style = MaterialTheme.typography.titleMedium,
                                color = ActionOrange
                            )
                        }
                    }
                }

                // Pilih Jam Ambil (Wajib pilih 1)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = ActionOrange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pilih Jam Ambil (Wajib Pilih 1)",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        slots.forEach { slot ->
                            val isSelected = selectedSlot == slot
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) ActionOrangeSoft else BgLightGray,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ActionOrange else SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectSlot(slot) }
                                    .testTag("slot_$slot")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (isSelected) ActionOrangeDark else TextBlack
                                    )
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) ActionOrange else TextGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Auto-cancel rule reminder
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StatusAmberSoft,
                    border = BorderStroke(1.dp, ActionOrangeBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = ActionOrangeDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Aturan Batal Otomatis: Jika tidak diambil lewat 15 menit dari slot jam pilihan, pesanan otomatis batal, uang kembali ke Saldo LUDES, dan kantong muncul lagi di Beranda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentScreen(
    product: ProductEntity,
    pickupSlot: String,
    walletBalanceRp: Int,
    selectedMethod: PaymentMethod,
    onSelectMethod: (PaymentMethod) -> Unit,
    onBack: () -> Unit,
    onConfirmPay: () -> Unit
) {
    val serviceFee = 1000
    val totalPay = product.discountPrice + serviceFee

    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Pembayaran Aman LUDES",
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
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    PrimaryOrangeButton(
                        text = "BAYAR ${formatRupiah(totalPay)}",
                        onClick = onConfirmPay,
                        testTag = "pay_now_button",
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
                // Rincian Total
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rincian Pembayaran",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${product.storeName} • Ambil $pickupSlot",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FreshGreenDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Harga Kantong", style = MaterialTheme.typography.bodyLarge, color = TextGray)
                            Text(formatRupiah(product.discountPrice), style = MaterialTheme.typography.bodyLarge, color = TextBlack)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Biaya Layanan", style = MaterialTheme.typography.bodyLarge, color = TextGray)
                            Text(formatRupiah(serviceFee), style = MaterialTheme.typography.bodyLarge, color = TextBlack)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SurfaceBorder)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Bayar",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextBlack
                            )
                            Text(
                                text = formatRupiah(totalPay),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = ActionOrange
                            )
                        }
                    }
                }

                // In-app payment guarantee (NO COD)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FreshGreenSoft,
                    border = BorderStroke(1.dp, FreshGreenBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = FreshGreenDark
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Pembayaran wajib lewat aplikasi LUDES (Tidak ada COD / bayar cash di warung). Dana diteruskan ke warung setelah QR kamu di-scan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextBlack
                        )
                    }
                }

                // Payment Methods
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pilih Metode Pembayaran",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        PaymentMethod.entries.forEach { method ->
                            val isSelected = selectedMethod == method
                            val subtitle = if (method == PaymentMethod.SALDO_SISA_ENAK) {
                                "Saldo kamu: ${formatRupiah(walletBalanceRp)}"
                            } else {
                                method.subtitle
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) ActionOrangeSoft else BgLightGray,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ActionOrange else SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectMethod(method) }
                                    .testTag("payment_method_${method.name}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 56.dp)
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Payments,
                                        contentDescription = null,
                                        tint = if (isSelected) ActionOrange else FreshGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextBlack
                                        )
                                        Text(
                                            text = subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextGray
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) ActionOrange else TextGray
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

@Composable
fun OrderSuccessScreen(
    order: OrderEntity,
    onContactStore: () -> Unit,
    onViewOrders: () -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold(
        containerColor = BgLightGray,
        topBar = {
            SubScreenTopBar(
                title = "Status Pesanan",
                onBack = onBackHome
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
                    color = FreshGreenSoft,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Sukses",
                            tint = FreshGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Text(
                    text = "Pesanan Diproses!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextBlack
                )
                Text(
                    text = "Nomor Pesanan ${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = ActionOrange
                )

                // Large QR Code in center
                QrCodeCard(code = order.id, sizeDp = 190.dp)

                // Pickup Info Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Detail Pengambilan (Ambil Sendiri)",
                            style = MaterialTheme.typography.titleSmall,
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
                            Icon(Icons.Filled.Storefront, contentDescription = null, tint = FreshGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${order.storeName} • ${order.storeAddress}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextBlack
                            )
                        }
                    }
                }

                // 3-Stage Status Stepper
                ThreeStageStatusCard(
                    stageIndex = if (order.isReadyForPickup) 1 else 0
                )

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
                        Text("HUBUNGI TOKO", style = MaterialTheme.typography.labelLarge)
                    }

                    SecondaryGreenButton(
                        text = "LIHAT PESANAN",
                        onClick = onViewOrders,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeStageStatusCard(stageIndex: Int) {
    val stages = listOf("Diproses", "Siap Diambil", "Selesai")
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BgWhite,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status 3 Tahap",
                style = MaterialTheme.typography.titleSmall,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stages.forEachIndexed { idx, label ->
                    val active = idx <= stageIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (active) FreshGreen else SurfaceBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = label,
                                tint = if (active) Color.White else TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) FreshGreenDark else TextGray
                        )
                    }
                }
            }
        }
    }
}
