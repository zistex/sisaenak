package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodCategory
import com.example.data.ProductEntity
import com.example.data.formatRupiah
import com.example.data.parseDistanceKm
import com.example.ui.SisaEnakUiState
import com.example.ui.components.FoodBagCard
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.ActionOrangeDark
import com.example.ui.theme.ActionOrangeSoft
import com.example.ui.theme.BgLightGray
import com.example.ui.theme.BgWhite
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import kotlin.math.roundToInt

private enum class SearchSortMode(val label: String) {
    RELEVAN("Relevan"),
    TERDEKAT("Terdekat"),
    TERMURAH("Termurah"),
    RATING("Rating Tertinggi")
}

private data class DistancePreset(val label: String, val maxKm: Double?)
private data class PricePreset(val label: String, val maxPrice: Int?)

@Composable
fun SearchScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onOpenProductDetail: (ProductEntity) -> Unit,
    onQuickBuy: (ProductEntity) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }
    var maxDistanceKm by rememberSaveable { mutableStateOf<Double?>(null) }
    var maxPriceRp by rememberSaveable { mutableStateOf<Int?>(null) }
    var sortModeName by rememberSaveable { mutableStateOf(SearchSortMode.RELEVAN.name) }
    var showFilterPanel by rememberSaveable { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    val selectedCategory = selectedCategoryName?.let { name ->
        FoodCategory.entries.find { it.name == name }
    }
    val sortMode = SearchSortMode.entries.find { it.name == sortModeName } ?: SearchSortMode.RELEVAN

    val distancePresets = remember {
        listOf(
            DistancePreset("Semua", null),
            DistancePreset("≤ 0.7 km", 0.7),
            DistancePreset("≤ 1.0 km", 1.0),
            DistancePreset("≤ 1.5 km", 1.5)
        )
    }

    val pricePresets = remember {
        listOf(
            PricePreset("Semua", null),
            PricePreset("≤ Rp10rb", 10000),
            PricePreset("≤ Rp12rb", 12000),
            PricePreset("≤ Rp15rb", 15000),
            PricePreset("≤ Rp20rb", 20000)
        )
    }

    val quickKeywords = remember {
        listOf("Roti", "Nasi", "Martabak", "Buah", "Kopi", "Dago", "Hemat")
    }

    val activeFilterCount by remember(selectedCategory, maxDistanceKm, maxPriceRp, sortMode) {
        derivedStateOf {
            var count = 0
            if (selectedCategory != null) count++
            if (maxDistanceKm != null) count++
            if (maxPriceRp != null) count++
            if (sortMode != SearchSortMode.RELEVAN) count++
            count
        }
    }

    val filteredResults by remember(
        uiState.products,
        query,
        selectedCategory,
        maxDistanceKm,
        maxPriceRp,
        sortMode
    ) {
        derivedStateOf {
            val q = query.trim().lowercase()
            val matched = uiState.products.filter { item ->
                val catLabel = FoodCategory.entries
                    .find { it.name == item.category }
                    ?.label
                    ?.lowercase()
                    .orEmpty()

                val matchesQuery = q.isEmpty() ||
                    item.title.lowercase().contains(q) ||
                    item.storeName.lowercase().contains(q) ||
                    item.description.lowercase().contains(q) ||
                    item.storeAddress.lowercase().contains(q) ||
                    item.pickupWindow.lowercase().contains(q) ||
                    item.liveSummary.lowercase().contains(q) ||
                    item.category.lowercase().contains(q) ||
                    catLabel.contains(q)

                val matchesCategory = selectedCategory == null || item.category == selectedCategory.name
                val itemDistanceKm = parseDistanceKm(item.distanceText)
                val matchesDistance = maxDistanceKm == null || itemDistanceKm <= (maxDistanceKm!! + 0.001)
                val matchesPrice = maxPriceRp == null || item.discountPrice <= maxPriceRp!!

                matchesQuery && matchesCategory && matchesDistance && matchesPrice
            }

            when (sortMode) {
                SearchSortMode.RELEVAN -> matched
                SearchSortMode.TERDEKAT -> matched.sortedBy { parseDistanceKm(it.distanceText) }
                SearchSortMode.TERMURAH -> matched.sortedBy { it.discountPrice }
                SearchSortMode.RATING -> matched.sortedByDescending { it.rating }
            }
        }
    }

    fun resetAll() {
        query = ""
        selectedCategoryName = null
        maxDistanceKm = null
        maxPriceRp = null
        sortModeName = SearchSortMode.RELEVAN.name
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgLightGray)
    ) {
        // Top Search Header
        Surface(
            color = BgWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
                            tint = TextBlack
                        )
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                text = "Cari roti, nasi, warung, lokasi...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                maxLines = 1
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = ActionOrange
                            )
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Hapus kata kunci",
                                        tint = TextGray
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgLightGray,
                            unfocusedContainerColor = BgLightGray,
                            focusedBorderColor = ActionOrange,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .testTag("search_screen_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = if (showFilterPanel || activeFilterCount > 0) ActionOrangeSoft else BgLightGray,
                        border = BorderStroke(
                            width = if (activeFilterCount > 0) 1.5.dp else 1.dp,
                            color = if (showFilterPanel || activeFilterCount > 0) ActionOrange else SurfaceBorder
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { showFilterPanel = !showFilterPanel }
                            .testTag("toggle_filter_panel")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Filter jarak & harga",
                                tint = if (showFilterPanel || activeFilterCount > 0) ActionOrangeDark else TextBlack,
                                modifier = Modifier.size(22.dp)
                            )
                            if (activeFilterCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(ActionOrange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeFilterCount.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Keyword Suggestions
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(quickKeywords) { keyword ->
                        val isSelected = query.equals(keyword, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) ActionOrangeSoft else BgWhite,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ActionOrange else SurfaceBorder
                            ),
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    query = if (isSelected) "" else keyword
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = keyword,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) ActionOrangeDark else TextBlack
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scrollable Filter Panel + Search Results
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Collapsible Filter Card (Jarak, Harga, Kategori, Urutkan)
                item {
                    androidx.compose.animation.AnimatedVisibility(visible = showFilterPanel) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Header + Reset
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Filter Pencarian",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        ),
                                        color = TextBlack
                                    )
                                    if (activeFilterCount > 0 || query.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = ActionOrangeSoft,
                                            border = BorderStroke(1.dp, ActionOrange.copy(alpha = 0.4f)),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .clickable { resetAll() }
                                                .testTag("search_reset_all")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.RestartAlt,
                                                    contentDescription = null,
                                                    tint = ActionOrangeDark,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Reset",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = ActionOrangeDark
                                                )
                                            }
                                        }
                                    }
                                }

                                // 1. Filter Jarak
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = FreshGreenDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Jarak Maksimal (Ambil Sendiri)",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = TextBlack
                                        )
                                    }
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(distancePresets) { preset ->
                                            val selected = maxDistanceKm == preset.maxKm
                                            SearchBorderFilterChip(
                                                label = preset.label,
                                                selected = selected,
                                                onClick = { maxDistanceKm = preset.maxKm },
                                                testTag = "filter_dist_${preset.label}"
                                            )
                                        }
                                    }
                                }

                                // 2. Filter Harga + Slider
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Payments,
                                                contentDescription = null,
                                                tint = ActionOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Harga Maksimal",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                color = TextBlack
                                            )
                                        }
                                        Text(
                                            text = maxPriceRp?.let { "≤ ${formatRupiah(it)}" } ?: "Semua Harga",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = if (maxPriceRp != null) ActionOrangeDark else FreshGreenDark
                                        )
                                    }
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(pricePresets) { preset ->
                                            val selected = maxPriceRp == preset.maxPrice
                                            SearchBorderFilterChip(
                                                label = preset.label,
                                                selected = selected,
                                                onClick = { maxPriceRp = preset.maxPrice },
                                                testTag = "filter_price_${preset.label}"
                                            )
                                        }
                                    }
                                    Slider(
                                        value = (maxPriceRp ?: 25000).toFloat(),
                                        onValueChange = { raw ->
                                            val stepped = ((raw / 1000f).roundToInt() * 1000).coerceIn(10000, 25000)
                                            maxPriceRp = if (stepped >= 25000) null else stepped
                                        },
                                        valueRange = 10000f..25000f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = ActionOrange,
                                            activeTrackColor = ActionOrange,
                                            inactiveTrackColor = SurfaceBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // 3. Filter Kategori (Border-only active state)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Kategori Makanan",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = TextBlack
                                    )
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        item {
                                            SearchBorderFilterChip(
                                                label = "Semua",
                                                selected = selectedCategory == null,
                                                onClick = { selectedCategoryName = null },
                                                testTag = "filter_cat_semua"
                                            )
                                        }
                                        items(FoodCategory.entries) { cat ->
                                            val selected = selectedCategory == cat
                                            SearchBorderFilterChip(
                                                label = cat.label,
                                                selected = selected,
                                                onClick = {
                                                    selectedCategoryName = if (selected) null else cat.name
                                                },
                                                testTag = "filter_cat_${cat.name.lowercase()}"
                                            )
                                        }
                                    }
                                }

                                // 4. Urutkan
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Sort,
                                            contentDescription = null,
                                            tint = TextGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Urutkan",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = TextBlack
                                        )
                                    }
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(SearchSortMode.entries) { mode ->
                                            SearchBorderFilterChip(
                                                label = mode.label,
                                                selected = sortMode == mode,
                                                onClick = { sortModeName = mode.name },
                                                testTag = "filter_sort_${mode.name.lowercase()}"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Results Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (query.isBlank()) {
                                "Hasil Pencarian"
                            } else {
                                "Hasil untuk \"${query.trim()}\""
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextBlack
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = FreshGreenSoft
                        ) {
                            Text(
                                text = "Ditemukan ${filteredResults.size} kantong",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FreshGreenDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (filteredResults.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
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
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Tidak ada kantong yang cocok",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextBlack,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Coba longgarkan filter jarak/harga atau gunakan kata kunci lain.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { resetAll() },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ActionOrange,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(
                                        text = "Reset Semua Filter",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(filteredResults, key = { it.id }) { product ->
                        FoodBagCard(
                            product = product,
                            onCardClick = { onOpenProductDetail(product) },
                            onBuyClick = { onQuickBuy(product) },
                            onToggleFavorite = { onToggleFavorite(product.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun SearchBorderFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = BgWhite,
        border = BorderStroke(
            width = if (selected) 1.dp else 0.8.dp,
            color = if (selected) ActionOrange else SurfaceBorder
        ),
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                ),
                color = if (selected) ActionOrangeDark else TextBlack
            )
        }
    }
}
