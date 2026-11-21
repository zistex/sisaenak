package com.example.ui.screens

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.FoodCategory
import com.example.data.ProductEntity
import com.example.data.formatRupiah
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodBagCard
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
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: SisaEnakUiState,
    onOpenSearch: () -> Unit,
    onToggleCategory: (FoodCategory) -> Unit,
    onOpenProductDetail: (ProductEntity) -> Unit,
    onQuickBuy: (ProductEntity) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenLiveTab: () -> Unit,
    onOpenWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstName = uiState.userPrefs.userName.trim().substringBefore(" ").ifBlank { "Sahabat" }
    val totalStock = uiState.products.sumOf { it.stock }

    val displayedProducts = if (uiState.selectedCategory == null) {
        uiState.products
    } else {
        uiState.products.filter { it.category == uiState.selectedCategory.name }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgLightGray),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Modern Personalized Greeting + Live Availability Badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Halo, $firstName!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 23.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = TextBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Selamatkan makanan enak dekat ${uiState.userPrefs.location}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FreshGreenSoft,
                        border = BorderStroke(1.dp, FreshGreenBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = FreshGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalStock siap",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = FreshGreenDark
                            )
                        }
                    }
                }
            }

            // Clickable Search Trigger Pill -> Opens Dedicated SearchScreen
            item {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onOpenSearch() }
                        .testTag("home_search_trigger")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buka pencarian",
                            tint = ActionOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cari roti, nasi, warung, atau lokasi...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ActionOrangeSoft,
                            border = BorderStroke(1.dp, ActionOrange.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "Filter pencarian",
                                    tint = ActionOrangeDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Filter",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ActionOrangeDark
                                )
                            }
                        }
                    }
                }
            }

            // Modern Multi-Info Hero Banner Carousel
            item {
                ModernHeroBannerCarousel(
                    uiState = uiState,
                    onClickPromoSlide = {
                        val firstMatch = uiState.products.firstOrNull { it.stock > 0 }
                        if (firstMatch != null) onOpenProductDetail(firstMatch)
                    },
                    onClickWalletSlide = onOpenWallet,
                    onClickLiveSlide = onOpenLiveTab
                )
            }

            // Modern Trust & Benefit Mini-Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HighlightInfoChip(
                        icon = Icons.Filled.Storefront,
                        label = "Ambil Sendiri",
                        sublabel = "Gratis 0 Ongkir",
                        tint = FreshGreenDark,
                        bg = FreshGreenSoft,
                        modifier = Modifier.weight(1f)
                    )
                    HighlightInfoChip(
                        icon = Icons.Filled.Verified,
                        label = "Bayar Aman",
                        sublabel = "QRIS & E-Wallet",
                        tint = ActionOrangeDark,
                        bg = ActionOrangeSoft,
                        modifier = Modifier.weight(1f)
                    )
                    HighlightInfoChip(
                        icon = Icons.Filled.Shield,
                        label = "Anti Rugi",
                        sublabel = "Garansi Refund",
                        tint = Color(0xFF1D4ED8),
                        bg = Color(0xFFEFF6FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Filter Strip (Active Uses Border Only)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pilih Kategori",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextBlack
                            )
                            Text(
                                text = "Tap kategori untuk saring kantong hemat",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                        if (uiState.selectedCategory != null) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ActionOrangeSoft,
                                border = BorderStroke(1.dp, ActionOrange.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onToggleCategory(uiState.selectedCategory) }
                            ) {
                                Text(
                                    text = "Reset Filter",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ActionOrangeDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item(key = "cat_semua") {
                            CategoryFilterCard(
                                label = "Semua",
                                icon = Icons.Filled.Apps,
                                badgeBg = FreshGreenSoft,
                                badgeTint = FreshGreenDark,
                                selected = uiState.selectedCategory == null,
                                onClick = {
                                    uiState.selectedCategory?.let { onToggleCategory(it) }
                                },
                                testTag = "cat_semua"
                            )
                        }
                        items(FoodCategory.entries, key = { it.name }) { cat ->
                            val (icon, badgeBg, badgeTint) = when (cat) {
                                FoodCategory.ROTI -> Triple(
                                    Icons.Filled.BakeryDining,
                                    Color(0xFFFFF3E0),
                                    Color(0xFFD97706)
                                )
                                FoodCategory.NASI -> Triple(
                                    Icons.Filled.RiceBowl,
                                    FreshGreenSoft,
                                    FreshGreenDark
                                )
                                FoodCategory.JAJANAN -> Triple(
                                    Icons.Filled.Cookie,
                                    Color(0xFFFDF2F8),
                                    Color(0xFFDB2777)
                                )
                                FoodCategory.BUAH -> Triple(
                                    Icons.Filled.Eco,
                                    Color(0xFFECFDF5),
                                    Color(0xFF059669)
                                )
                                FoodCategory.LAINNYA -> Triple(
                                    Icons.Filled.Fastfood,
                                    Color(0xFFEEF2FF),
                                    Color(0xFF4F46E5)
                                )
                            }
                            CategoryFilterCard(
                                label = cat.label,
                                icon = icon,
                                badgeBg = badgeBg,
                                badgeTint = badgeTint,
                                selected = uiState.selectedCategory == cat,
                                onClick = { onToggleCategory(cat) },
                                testTag = "cat_${cat.name.lowercase()}"
                            )
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.selectedCategory != null) {
                            "Kantong ${uiState.selectedCategory.label} Terdekat"
                        } else {
                            "Siap Diambil Malam Ini"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = TextBlack
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = FreshGreenSoft
                    ) {
                        Text(
                            text = "${displayedProducts.size} kantong",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = FreshGreenDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (displayedProducts.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada kantong di kategori ini",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextBlack,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Coba pilih kategori lain atau cari lewat halaman pencarian.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(displayedProducts, key = { it.id }) { product ->
                    FoodBagCard(
                        product = product,
                        onCardClick = { onOpenProductDetail(product) },
                        onBuyClick = { onQuickBuy(product) },
                        onToggleFavorite = { onToggleFavorite(product.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ModernHeroBannerCarousel(
    uiState: SisaEnakUiState,
    onClickPromoSlide: () -> Unit,
    onClickWalletSlide: () -> Unit,
    onClickLiveSlide: () -> Unit
) {
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(4500)
            val next = (pagerState.currentPage + 1) % pageCount
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_banner_pager")
        ) { page ->
            when (page) {
                // Slide 1: Promo Surplus Diskon s.d. 70%
                0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 168.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ActionOrange, ActionOrangeDark, Color(0xFFC2410C))
                                )
                            )
                            .clickable { onClickPromoSlide() }
                            .padding(18.dp)
                    ) {
                        // Decorative glass circles
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 34.dp, y = (-34).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        )
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = (-24).dp, y = 26.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.10f))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.22f)
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
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "AMBIL SENDIRI • TANPA ONGKIR",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Sisa Enak Malam Ini\nHemat Hingga 70%",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        lineHeight = 26.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Mulai Rp10.000/kantong • Fresh dari warung sekitar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Lihat Promo",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = ActionOrangeDark
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = ActionOrangeDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.White.copy(alpha = 0.18f))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_food_bakery),
                                    contentDescription = "Promo Roti Hemat",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = FreshGreen,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "70% OFF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Slide 2: Info Jejak Hemat & Saldo Dompet Pengguna
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 168.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(FreshGreenDark, Color(0xFF16A34A), FreshGreen)
                                )
                            )
                            .clickable { onClickWalletSlide() }
                            .padding(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 28.dp, y = (-28).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.22f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Eco,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "JEJAK HEMAT & DOMPETMU",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${uiState.userPrefs.bagsRescued} Kantong Diselamatkan",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.White.copy(alpha = 0.18f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Total Hemat",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                        Text(
                                            text = formatRupiah(uiState.userPrefs.totalSavedRp),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.White.copy(alpha = 0.18f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Saldo Sisa Enak",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                        Text(
                                            text = formatRupiah(uiState.userPrefs.walletBalanceRp),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Kelola Dompet & Tarik Saldo",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = FreshGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = FreshGreenDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Slide 3: Info Live Warung Real-Time
                else -> {
                    val liveCount = uiState.liveProducts.count { it.stock > 0 }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 168.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                                )
                            )
                            .clickable { onClickLiveSlide() }
                            .padding(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 30.dp, y = 30.dp)
                                .clip(CircleShape)
                                .background(LiveRed.copy(alpha = 0.18f))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = LiveRed
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LiveTv,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "$liveCount WARUNG SEDANG LIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Intip Kondisi Makanan\nSecara Real-Time",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        lineHeight = 25.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tanya penjual langsung & amankan stok sisa sebelum habis.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = ActionOrange
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Tonton Live Sekarang",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.White.copy(alpha = 0.14f))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_food_nasi),
                                    contentDescription = "Live Warung Bu Sari",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = LiveRed,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Modern Expanding Pill Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "heroIndicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) ActionOrange else SurfaceBorder)
                )
            }
        }
    }
}

@Composable
private fun HighlightInfoChip(
    icon: ImageVector,
    label: String,
    sublabel: String,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BgWhite,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = TextBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterCard(
    label: String,
    icon: ImageVector,
    badgeBg: Color,
    badgeTint: Color,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BgWhite,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) ActionOrange else SurfaceBorder
        ),
        shadowElevation = if (selected) 2.dp else 1.dp,
        modifier = Modifier
            .testTag(testTag)
            .width(84.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = badgeTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold
                ),
                color = if (selected) ActionOrangeDark else TextBlack,
                maxLines = 1
            )
        }
    }
}
