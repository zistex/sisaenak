package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.data.PaymentMethod
import com.example.data.ProductEntity
import com.example.data.formatRupiah
import com.example.ui.LiveComment
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodProductImage
import com.example.ui.components.PickupOnlyBanner
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.QrCodeCard
import com.example.ui.components.SecondaryGreenButton
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
import com.example.ui.theme.StarYellow
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class FloatingHeart(
    val id: Long,
    val startOffset: Offset
)

/**
 * Tab Live & Video — Persis TikTok Live!
 * Swipe atas/bawah antar warung Live dengan VerticalPager,
 * keranjang kuning/oranye + pinned product card + modal bottom sheet pesan langsung di tempat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTabScreen(
    uiState: SisaEnakUiState,
    onToggleFavorite: (String) -> Unit,
    onInitCheckout: (ProductEntity) -> Unit,
    onSelectSlot: (String) -> Unit,
    onSelectPayment: (PaymentMethod) -> Unit,
    onPlaceLiveOrder: (ProductEntity, (OrderEntity) -> Unit) -> Unit,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveList = uiState.liveProducts.ifEmpty { uiState.products.take(4) }
    val pagerState = rememberPagerState(pageCount = { liveList.size.coerceAtLeast(1) })
    val scope = rememberCoroutineScope()

    var showGridOverlay by remember { mutableStateOf(false) }
    var activeOrderSheetProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var completedLiveOrder by remember { mutableStateOf<OrderEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (liveList.isEmpty()) {
            Text(
                text = "Belum ada warung yang sedang Live saat ini.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val product = liveList[pageIndex]
                TikTokLiveStreamPage(
                    product = product,
                    pageIndex = pageIndex,
                    totalPages = liveList.size,
                    comments = uiState.liveComments,
                    onToggleFavorite = { onToggleFavorite(product.id) },
                    onOpenGrid = { showGridOverlay = true },
                    onOpenDirectOrder = {
                        onInitCheckout(product)
                        completedLiveOrder = null
                        activeOrderSheetProduct = product
                    }
                )
            }
        }

        // Optional Grid Switcher Overlay ("Daftar Live" ala TikTok Live Explore)
        AnimatedVisibility(
            visible = showGridOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LiveExploreGridOverlay(
                liveProducts = liveList,
                currentIndex = pagerState.currentPage,
                onSelectIndex = { idx ->
                    showGridOverlay = false
                    scope.launch { pagerState.animateScrollToPage(idx) }
                },
                onClose = { showGridOverlay = false }
            )
        }
    }

    // TikTok Shop-style Instant Direct Order Bottom Sheet (Pesan Langsung di Dalam Live!)
    val sheetProduct = activeOrderSheetProduct
    if (sheetProduct != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = {
                activeOrderSheetProduct = null
                completedLiveOrder = null
            },
            sheetState = sheetState,
            containerColor = BgWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val doneOrder = completedLiveOrder
            if (doneOrder == null) {
                LiveDirectOrderSheetContent(
                    product = sheetProduct,
                    selectedSlot = uiState.selectedPickupSlot,
                    selectedMethod = uiState.selectedPaymentMethod,
                    walletBalanceRp = uiState.userPrefs.walletBalanceRp,
                    onSelectSlot = onSelectSlot,
                    onSelectMethod = onSelectPayment,
                    onClose = { activeOrderSheetProduct = null },
                    onConfirmPay = {
                        onPlaceLiveOrder(sheetProduct) { created ->
                            completedLiveOrder = created
                        }
                    }
                )
            } else {
                LiveOrderSuccessSheetContent(
                    order = doneOrder,
                    onViewOrders = {
                        activeOrderSheetProduct = null
                        completedLiveOrder = null
                        onNavigateToOrders()
                    },
                    onContinueWatching = {
                        activeOrderSheetProduct = null
                        completedLiveOrder = null
                    }
                )
            }
        }
    }
}

@Composable
private fun TikTokLiveStreamPage(
    product: ProductEntity,
    pageIndex: Int,
    totalPages: Int,
    comments: List<LiveComment>,
    onToggleFavorite: () -> Unit,
    onOpenGrid: () -> Unit,
    onOpenDirectOrder: () -> Unit
) {
    val context = LocalContext.current
    var showLiveChat by remember { mutableStateOf(true) }
    var likeCount by remember(product.id) { mutableIntStateOf(1240 + pageIndex * 180) }
    var isLiked by remember(product.id) { mutableStateOf(false) }
    val floatingHearts = remember { mutableStateListOf<FloatingHeart>() }

    val infiniteTransition = rememberInfiniteTransition(label = "tiktokLive")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.02f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoomScale"
    )
    val basketPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "basketPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(product.id) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        isLiked = true
                        likeCount += 1
                        floatingHearts.add(FloatingHeart(System.nanoTime(), tapOffset))
                    }
                )
            }
    ) {
        // 1. Full-bleed Live Video Canvas
        FoodProductImage(
            imageKey = product.imageKey,
            customImageUri = product.customImageUri,
            contentDescription = product.storeName,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                }
        )

        // Top & Bottom Scrim Gradients (TikTok signature)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.58f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // Floating Double-Tap Hearts
        floatingHearts.forEach { heart ->
            FloatingHeartParticle(
                heart = heart,
                onFinished = { floatingHearts.remove(heart) }
            )
        }

        // 2. Top TikTok Live Header Overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Host Pill (Avatar + Nama Warung + Ikuti/Favorit)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, LiveRed, CircleShape)
                        ) {
                            FoodProductImage(
                                imageKey = product.imageKey,
                                customImageUri = product.customImageUri,
                                contentDescription = product.storeName,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.widthIn(max = 150.dp)) {
                            Text(
                                text = product.storeName,
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = FreshGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${product.distanceText} • Ambil Sendiri",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (product.isFavorite) FreshGreen else ActionOrange,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onToggleFavorite() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (product.isFavorite) Icons.Filled.Check else Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (product.isFavorite) "Disukai" else "Ikuti",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Badges: LIVE + Viewers + Grid Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = LiveRed
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.55f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = product.liveViewersText.ifBlank { "1.2rb" }
                                    .replace(" penonton", ""),
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                color = Color.White
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { onOpenGrid() }
                            .testTag("live_grid_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.GridView,
                                contentDescription = "Semua Live",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second-row TikTok Live badge: Lokasi Ambil Sendiri + Swipe hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = FreshGreen.copy(alpha = 0.90f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ambil Sendiri • ${product.storeAddress}",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            color = Color.White
                        )
                    }
                }

                if (totalPages > 1) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Geser (${pageIndex + 1}/$totalPages)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. Bottom Area: Left (Chat + Pinned TikTok Shop Card + Comment Bar) & Right (TikTok Vertical Rail)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // LEFT SIDE: Running Chat + Pinned Product Card
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Running Chat Stream (toggled via comment icon on right rail)
                    AnimatedVisibility(visible = showLiveChat) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(132.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            reverseLayout = true
                        ) {
                            items(comments.reversed(), key = { it.id }) { comment ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.Black.copy(alpha = 0.48f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "${comment.author}: ",
                                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                            color = FreshGreen
                                        )
                                        Text(
                                            text = comment.text,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pinned TikTok-Shop-Style Product Card (Bisa Pesan Langsung!)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BgWhite.copy(alpha = 0.96f),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDirectOrder() }
                            .testTag("live_pinned_product_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(62.dp)) {
                                FoodProductImage(
                                    imageKey = product.imageKey,
                                    customImageUri = product.customImageUri,
                                    contentDescription = product.title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Surface(
                                    shape = RoundedCornerShape(bottomEnd = 8.dp, topStart = 12.dp),
                                    color = LiveRed,
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "01",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.liveSummary.ifBlank { "2 roti + 1 nasi, Hemat 40%" },
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                                    color = TextBlack,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Sisa ${product.stock} Kantong • Ambil Sendiri",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = FreshGreenDark
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatRupiah(product.discountPrice),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = ActionOrange
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatRupiah(product.originalPrice),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            textDecoration = TextDecoration.LineThrough
                                        ),
                                        color = TextGray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = onOpenDirectOrder,
                                enabled = product.stock > 0,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ActionOrange,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.heightIn(min = 42.dp)
                            ) {
                                Text(
                                    text = if (product.stock > 0) "BELI" else "HABIS",
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // RIGHT SIDE: Classic TikTok Vertical Action Rail
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    // Heart / Like
                    TikTokRailButton(
                        icon = Icons.Filled.Favorite,
                        label = "$likeCount",
                        tint = if (isLiked) LiveRed else Color.White,
                        onClick = {
                            isLiked = true
                            likeCount += 1
                            floatingHearts.add(FloatingHeart(System.nanoTime(), Offset(500f, 900f)))
                        }
                    )

                    // Comment Visibility Toggle
                    TikTokRailButton(
                        icon = Icons.Filled.ModeComment,
                        label = "${comments.size}",
                        tint = if (showLiveChat) Color.White else Color.White.copy(alpha = 0.55f),
                        onClick = { showLiveChat = !showLiveChat }
                    )

                    // Share Live
                    TikTokRailButton(
                        icon = Icons.Filled.Share,
                        label = "Bagikan",
                        tint = Color.White,
                        onClick = {
                            val text = "Nonton Live ${product.storeName} di Sisa Enak yuk! Kantong hemat cuma ${formatRupiah(product.discountPrice)}, ambil sendiri di ${product.storeAddress}."
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(intent, "Bagikan Live"))
                            }
                        }
                    )

                    // Pulsing Keranjang Kuning / Oranye (Buka Pesan Langsung)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onOpenDirectOrder() }
                            .testTag("live_basket_rail_button")
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Surface(
                                shape = CircleShape,
                                color = StarYellow,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(basketPulse)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.ShoppingCart,
                                        contentDescription = "Keranjang Live",
                                        tint = TextBlack,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = LiveRed,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${product.stock}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Keranjang",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TikTokRailButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.35f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color.White
        )
    }
}

@Composable
private fun FloatingHeartParticle(
    heart: FloatingHeart,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    val density = LocalDensity.current
    val risePx = with(density) { 180.dp.toPx() }

    LaunchedEffect(heart.id) {
        progress.animateTo(1f, animationSpec = tween(850, easing = LinearEasing))
        onFinished()
    }

    val yOffset = (heart.startOffset.y - progress.value * risePx).roundToInt()
    val xOffset = heart.startOffset.x.roundToInt() - 40
    val scale = 0.8f + (1f - progress.value) * 0.6f
    val alpha = (1f - progress.value).coerceIn(0f, 1f)

    Icon(
        imageVector = Icons.Filled.Favorite,
        contentDescription = null,
        tint = LiveRed,
        modifier = Modifier
            .offset { IntOffset(xOffset, yOffset) }
            .size(56.dp)
            .scale(scale)
            .alpha(alpha)
    )
}

/**
 * In-Stream TikTok Shop Direct Order Bottom Sheet:
 * User memilih jam ambil + metode pembayaran (tanpa COD) langsung di atas Live video!
 */
@Composable
private fun LiveDirectOrderSheetContent(
    product: ProductEntity,
    selectedSlot: String,
    selectedMethod: PaymentMethod,
    walletBalanceRp: Int,
    onSelectSlot: (String) -> Unit,
    onSelectMethod: (PaymentMethod) -> Unit,
    onClose: () -> Unit,
    onConfirmPay: () -> Unit
) {
    val slots = product.pickupSlotsCsv
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .ifEmpty { listOf("17.00-17.30", "17.30-18.00") }

    val serviceFee = 1000
    val totalPay = product.discountPrice + serviceFee
    val liveMethods = listOf(
        PaymentMethod.QRIS,
        PaymentMethod.GOPAY,
        PaymentMethod.OVO,
        PaymentMethod.SALDO_SISA_ENAK
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = ActionOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pesan Langsung dari Live",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = TextGray)
            }
        }

        // Product Snapshot
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BgLightGray,
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
                        .size(70.dp)
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
                        text = product.liveSummary.ifBlank { "2 roti + 1 nasi, Hemat 40%" },
                        style = MaterialTheme.typography.bodySmall,
                        color = FreshGreenDark
                    )
                    Text(
                        text = "Sisa ${product.stock} kantong",
                        style = MaterialTheme.typography.labelMedium,
                        color = ActionOrangeDark
                    )
                }
            }
        }

        // Info Ambil Sendiri (No delivery, no ongkir)
        PickupOnlyBanner(
            title = "Ambil Sendiri - ${product.storeName} ${product.distanceText}",
            subtitle = "${product.storeAddress} • Gratis tanpa ongkir, tidak ada COD"
        )

        // Pilih Jam Ambil
        Text(
            text = "Pilih Jam Ambil",
            style = MaterialTheme.typography.titleSmall,
            color = TextBlack
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slots.take(3).forEach { slot ->
                val isSelected = selectedSlot == slot
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ActionOrangeSoft else BgLightGray,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) ActionOrange else SurfaceBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectSlot(slot) }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) ActionOrangeDark else TextBlack
                        )
                    }
                }
            }
        }

        // Pilih Pembayaran (Tanpa COD)
        Text(
            text = "Bayar Lewat Aplikasi (Tanpa COD)",
            style = MaterialTheme.typography.titleSmall,
            color = TextBlack
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            liveMethods.forEach { method ->
                val isSelected = selectedMethod == method
                val sub = if (method == PaymentMethod.SALDO_SISA_ENAK) {
                    "Saldo ${formatRupiah(walletBalanceRp)}"
                } else {
                    method.subtitle
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) ActionOrangeSoft else BgLightGray,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) ActionOrange else SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectMethod(method) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payments,
                            contentDescription = null,
                            tint = if (isSelected) ActionOrange else FreshGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(method.label, style = MaterialTheme.typography.labelLarge, color = TextBlack)
                            Text(sub, style = MaterialTheme.typography.bodySmall, color = TextGray)
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

        // Price Breakdown
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = FreshGreenSoft,
            border = BorderStroke(1.dp, FreshGreenBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kantong", style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                    Text(formatRupiah(product.discountPrice), style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Biaya Layanan", style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                    Text(formatRupiah(serviceFee), style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = FreshGreenBorder)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Bayar", style = MaterialTheme.typography.titleSmall, color = TextBlack)
                    Text(
                        text = formatRupiah(totalPay),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = ActionOrange
                    )
                }
            }
        }

        PrimaryOrangeButton(
            text = "BAYAR ${formatRupiah(totalPay)} SEKARANG",
            enabled = selectedSlot.isNotBlank() && product.stock > 0,
            onClick = onConfirmPay,
            testTag = "live_sheet_pay_button",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * In-Stream Success QR Ticket right inside the TikTok Live Bottom Sheet!
 */
@Composable
private fun LiveOrderSuccessSheetContent(
    order: OrderEntity,
    onViewOrders: () -> Unit,
    onContinueWatching: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(bottom = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = FreshGreen,
            modifier = Modifier.size(58.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Berhasil",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Text(
            text = "Yeay Kamu Dapat Kantong dari Live!",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 19.sp),
            color = TextBlack
        )

        QrCodeCard(code = order.id, sizeDp = 165.dp)

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BgLightGray,
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = ActionOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jam Ambil: ${order.pickupSlot}", style = MaterialTheme.typography.titleSmall, color = TextBlack)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Storefront, contentDescription = null, tint = FreshGreenDark, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${order.storeName} • ${order.storeAddress}", style = MaterialTheme.typography.bodyMedium, color = TextBlack)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryGreenButton(
                text = "Lihat Pesanan",
                onClick = onViewOrders,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = {
                    val shareText = "Aku baru saja menyelamatkan makanan dari Live ${order.storeName} di Sisa Enak cuma ${formatRupiah(order.bagPrice)}! Ambil sendiri di ${order.storeAddress}."
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    runCatching {
                        context.startActivity(Intent.createChooser(sendIntent, "Bagikan ke Teman"))
                    }
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, ActionOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ActionOrangeDark),
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bagikan ke Teman", style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp))
            }
        }

        Button(
            onClick = onContinueWatching,
            colors = ButtonDefaults.textButtonColors(contentColor = TextGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lanjut Nonton Live")
        }
    }
}

/**
 * Overlay Daftar Live (2-Column Grid) yang bisa dibuka kapan saja dari tombol Grid di pojok kanan atas Live.
 */
@Composable
private fun LiveExploreGridOverlay(
    liveProducts: List<ProductEntity>,
    currentIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.96f),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = RoundedCornerShape(6.dp), color = LiveRed) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = "Pilih Siaran Warung Live",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Tutup daftar", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                itemsIndexed(liveProducts) { idx, product ->
                    val isCurrent = idx == currentIndex
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = if (isCurrent) BorderStroke(2.dp, ActionOrange) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .clickable { onSelectIndex(idx) }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            FoodProductImage(
                                imageKey = product.imageKey,
                                customImageUri = product.customImageUri,
                                contentDescription = product.storeName,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(6.dp), color = LiveRed) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(shape = RoundedCornerShape(50), color = Color.Black.copy(alpha = 0.55f)) {
                                    Text(
                                        text = product.liveViewersText.ifBlank { "1.2rb penonton" },
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(50), color = ActionOrange) {
                                    Text(
                                        text = "Sisa ${product.stock} • ${formatRupiah(product.discountPrice)}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.storeName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${product.distanceText} • Ambil Sendiri",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
