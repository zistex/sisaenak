package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNotification
import com.example.ui.NotificationCategory
import com.example.ui.SisaEnakUiState
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
import com.example.ui.theme.LiveRedSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray

@Composable
fun NotificationsScreen(
    uiState: SisaEnakUiState,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onClickNotification: (AppNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedCategory = selectedCategoryName?.let { name ->
        NotificationCategory.entries.find { it.name == name }
    }

    val filteredNotifications by remember(uiState.notifications, selectedCategory) {
        derivedStateOf {
            if (selectedCategory == null) {
                uiState.notifications
            } else {
                uiState.notifications.filter { it.category == selectedCategory }
            }
        }
    }

    val recentItems = remember(filteredNotifications) {
        filteredNotifications.filter { it.isRecent }
    }
    val earlierItems = remember(filteredNotifications) {
        filteredNotifications.filter { !it.isRecent }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgLightGray)
    ) {
        SubScreenTopBar(
            title = "Notifikasi",
            onBack = onBack,
            trailing = {
                if (uiState.unreadNotificationCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = FreshGreenSoft,
                        border = BorderStroke(1.dp, FreshGreenBorder),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { onMarkAllRead() }
                            .testTag("notifications_mark_all_read")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = "Tandai semua dibaca",
                                tint = FreshGreenDark,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tandai dibaca",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FreshGreenDark
                            )
                        }
                    }
                }
            }
        )

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
                // Status Summary Strip
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (uiState.unreadNotificationCount > 0) ActionOrangeSoft else FreshGreenSoft
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (uiState.unreadNotificationCount > 0) {
                                            Icons.Filled.LocalOffer
                                        } else {
                                            Icons.Filled.CheckCircle
                                        },
                                        contentDescription = null,
                                        tint = if (uiState.unreadNotificationCount > 0) ActionOrangeDark else FreshGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (uiState.unreadNotificationCount > 0) {
                                            "${uiState.unreadNotificationCount} notifikasi belum dibaca"
                                        } else {
                                            "Semua notifikasi sudah dibaca"
                                        },
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextBlack
                                    )
                                    Text(
                                        text = "Pantau info pesanan, siaran Live, promo & saldo dompet",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Filter Category Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            NotificationFilterChip(
                                label = "Semua",
                                unreadCount = uiState.unreadNotificationCount,
                                selected = selectedCategory == null,
                                onClick = { selectedCategoryName = null }
                            )
                        }
                        items(NotificationCategory.entries) { cat ->
                            val unreadInCat = uiState.notifications.count { it.category == cat && !it.isRead }
                            val isSelected = selectedCategory == cat
                            NotificationFilterChip(
                                label = cat.label,
                                unreadCount = unreadInCat,
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryName = if (isSelected) null else cat.name
                                }
                            )
                        }
                    }
                }

                if (filteredNotifications.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(BgLightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NotificationsNone,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum ada notifikasi di kategori ini",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextBlack,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Kabar terbaru seputar pesanan, promo kantong hemat, dan siaran Live akan tampil di sini.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray,
                                    textAlign = TextAlign.Center
                                )
                                if (selectedCategory != null) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { selectedCategoryName = null },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ActionOrange,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.heightIn(min = 46.dp)
                                    ) {
                                        Text("Lihat Semua Notifikasi")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (recentItems.isNotEmpty()) {
                        item {
                            SectionLabelRow(title = "Terbaru", count = recentItems.size)
                        }
                        items(recentItems, key = { it.id }) { notif ->
                            NotificationListCard(
                                notification = notif,
                                onClick = { onClickNotification(notif) }
                            )
                        }
                    }

                    if (earlierItems.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(2.dp))
                            SectionLabelRow(title = "Sebelumnya", count = earlierItems.size)
                        }
                        items(earlierItems, key = { it.id }) { notif ->
                            NotificationListCard(
                                notification = notif,
                                onClick = { onClickNotification(notif) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        }
    }
}

@Composable
private fun SectionLabelRow(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            ),
            color = TextBlack
        )
        Text(
            text = "$count info",
            style = MaterialTheme.typography.labelMedium,
            color = TextGray
        )
    }
}

@Composable
private fun NotificationFilterChip(
    label: String,
    unreadCount: Int,
    selected: Boolean,
    onClick: () -> Unit
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
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                ),
                color = if (selected) ActionOrangeDark else TextBlack
            )
            if (unreadCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = if (selected) ActionOrange else ActionOrangeSoft
                ) {
                    Text(
                        text = "$unreadCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (selected) Color.White else ActionOrangeDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationListCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val (icon, iconBg, iconTint) = when (notification.category) {
        NotificationCategory.PESANAN -> Triple(
            Icons.Filled.ReceiptLong,
            FreshGreenSoft,
            FreshGreenDark
        )
        NotificationCategory.LIVE -> Triple(
            Icons.Filled.LiveTv,
            LiveRedSoft,
            LiveRed
        )
        NotificationCategory.PROMO -> Triple(
            Icons.Filled.LocalOffer,
            ActionOrangeSoft,
            ActionOrangeDark
        )
        NotificationCategory.DOMPET -> Triple(
            Icons.Filled.AccountBalanceWallet,
            Color(0xFFEFF6FF),
            Color(0xFF1D4ED8)
        )
    }

    val cardBg = if (notification.isRead) BgWhite else Color(0xFFFFFBF7)
    val cardBorder = if (notification.isRead) SurfaceBorder else ActionOrangeBorder

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (notification.isRead) 0.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("notif_card_${notification.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = notification.category.label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = iconBg
                        ) {
                            Text(
                                text = notification.category.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = iconTint,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = notification.timeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray
                        )
                    }

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ActionOrange)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 15.sp,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.ExtraBold
                    ),
                    color = TextBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = TextGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buka halaman terkait",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (notification.isRead) FreshGreenDark else ActionOrangeDark
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (notification.isRead) FreshGreenDark else ActionOrangeDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
