package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.OrderStatus
import com.example.ui.components.MainTab
import com.example.ui.components.SisaEnakBottomBar
import com.example.ui.components.SisaEnakTopHeader
import com.example.ui.screens.AccountTabScreen
import com.example.ui.screens.CheckoutPickupScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguageSettingsScreen
import com.example.ui.screens.LiveTabScreen
import com.example.ui.screens.LocationPickerScreen
import com.example.ui.screens.MerchantQrScannerScreen
import com.example.ui.screens.MerchantScreen
import com.example.ui.screens.NotificationSettingsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrderCompletedScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrderPickupStatusScreen
import com.example.ui.screens.OrderRatingScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OrdersTabScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.RegisterAuthScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ViewHistoryScreen
import com.example.ui.screens.VouchersScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.BgLightGray
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray

private object Routes {
    const val SPLASH = "splash"
    const val REGISTER = "register"

    const val BERANDA = "beranda"
    const val PESANAN = "pesanan"
    const val LIVE = "live"
    const val AKUN = "akun"

    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CHECKOUT = "checkout/{productId}"
    const val PAYMENT = "payment/{productId}"
    const val ORDER_SUCCESS = "order_success/{orderId}"

    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val ORDER_PICKUP = "order_pickup/{orderId}"
    const val ORDER_COMPLETED = "order_completed/{orderId}"
    const val ORDER_RATING = "order_rating/{orderId}"

    const val FAVORITES = "favorites"
    const val VIEW_HISTORY = "view_history"
    const val SETTINGS = "settings"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_LANGUAGE = "settings_language"
    const val SETTINGS_PRIVACY = "settings_privacy"
    const val WALLET = "wallet"
    const val VOUCHERS = "vouchers"
    const val MERCHANT = "merchant"
    const val MERCHANT_SCAN_QR = "merchant_scan_qr"
    const val SEARCH = "search"
    const val NOTIFICATIONS = "notifications"
    const val LOCATION_PICKER = "location_picker"

    fun productDetail(id: String) = "product_detail/${Uri.encode(id)}"
    fun checkout(id: String) = "checkout/${Uri.encode(id)}"
    fun payment(id: String) = "payment/${Uri.encode(id)}"
    fun orderSuccess(id: String) = "order_success/${Uri.encode(id)}"

    fun orderDetail(id: String) = "order_detail/${Uri.encode(id)}"
    fun orderPickup(id: String) = "order_pickup/${Uri.encode(id)}"
    fun orderCompleted(id: String) = "order_completed/${Uri.encode(id)}"
    fun orderRating(id: String) = "order_rating/${Uri.encode(id)}"
}

@Composable
fun SisaEnakApp(
    viewModel: SisaEnakViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var contactStoreInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.BERANDA
    val isMainTab = currentRoute in listOf(Routes.BERANDA, Routes.PESANAN, Routes.LIVE, Routes.AKUN)
    val activeOrderCount = uiState.orders.count { it.status == OrderStatus.BELUM_DIAMBIL.name }

    fun switchTab(tab: MainTab) {
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (isMainTab) {
                SisaEnakTopHeader(
                    location = uiState.userPrefs.location,
                    onLocationClick = { navController.navigate(Routes.LOCATION_PICKER) },
                    onBellClick = { navController.navigate(Routes.NOTIFICATIONS) },
                    unreadNotificationCount = uiState.unreadNotificationCount
                )
            }
        },
        bottomBar = {
            if (isMainTab) {
                SisaEnakBottomBar(
                    currentRoute = currentRoute,
                    hasLiveNow = uiState.hasLiveNow,
                    activeOrderCount = activeOrderCount,
                    onSelectTab = { switchTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(
                        isAuthReady = uiState.isAuthReady,
                        isLoggedIn = uiState.userPrefs.isLoggedIn,
                        onNavigateToHome = {
                            navController.navigate(Routes.BERANDA) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(Routes.REGISTER) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.REGISTER) {
                    RegisterAuthScreen(
                        onRegisterVerified = { name, email, password ->
                            viewModel.registerAndVerifyUser(name, email, password) {
                                navController.navigate(Routes.BERANDA) {
                                    popUpTo(Routes.REGISTER) { inclusive = true }
                                }
                            }
                        },
                        onLoginWithEmail = { email, password, onError ->
                            viewModel.loginWithEmail(
                                email = email,
                                password = password,
                                onSuccess = {
                                    navController.navigate(Routes.BERANDA) {
                                        popUpTo(Routes.REGISTER) { inclusive = true }
                                    }
                                },
                                onError = onError
                            )
                        },
                        onQuickDemoLogin = {
                            viewModel.loginDemoSiti {
                                navController.navigate(Routes.BERANDA) {
                                    popUpTo(Routes.REGISTER) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                // 1. TAB BERANDA
                composable(Routes.BERANDA) {
                    HomeScreen(
                        uiState = uiState,
                        onOpenSearch = { navController.navigate(Routes.SEARCH) },
                        onToggleCategory = { viewModel.toggleCategory(it) },
                        onOpenProductDetail = { product ->
                            viewModel.recordProductViewed(product.id)
                            navController.navigate(Routes.productDetail(product.id))
                        },
                        onQuickBuy = { product ->
                            viewModel.recordProductViewed(product.id)
                            viewModel.initCheckoutForProduct(product)
                            navController.navigate(Routes.checkout(product.id))
                        },
                        onToggleFavorite = { productId ->
                            viewModel.toggleProductFavorite(productId)
                        },
                        onOpenLiveTab = { switchTab(MainTab.LIVE) },
                        onOpenWallet = { navController.navigate(Routes.WALLET) }
                    )
                }

                // HALAMAN ATUR TITIK LOKASI (Marker Map)
                composable(Routes.LOCATION_PICKER) {
                    LocationPickerScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onConfirmLocation = { label, lat, lng ->
                            viewModel.updateLocation(label, lat, lng)
                            navController.popBackStack()
                        }
                    )
                }

                // HALAMAN NOTIFIKASI
                composable(Routes.NOTIFICATIONS) {
                    NotificationsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        onClickNotification = { notif ->
                            viewModel.markNotificationRead(notif.id)
                            when (val target = notif.target) {
                                is NotificationTarget.ProductDetail -> {
                                    viewModel.recordProductViewed(target.productId)
                                    navController.navigate(Routes.productDetail(target.productId))
                                }
                                NotificationTarget.OrdersTab -> switchTab(MainTab.PESANAN)
                                NotificationTarget.LiveTab -> switchTab(MainTab.LIVE)
                                NotificationTarget.Wallet -> navController.navigate(Routes.WALLET)
                                NotificationTarget.Vouchers -> navController.navigate(Routes.VOUCHERS)
                                NotificationTarget.Search -> navController.navigate(Routes.SEARCH)
                            }
                        }
                    )
                }

                // HALAMAN PENCARIAN & FILTER (Jarak, Harga, Kategori, Urutkan)
                composable(Routes.SEARCH) {
                    SearchScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onOpenProductDetail = { product ->
                            viewModel.recordProductViewed(product.id)
                            navController.navigate(Routes.productDetail(product.id))
                        },
                        onQuickBuy = { product ->
                            viewModel.recordProductViewed(product.id)
                            viewModel.initCheckoutForProduct(product)
                            navController.navigate(Routes.checkout(product.id))
                        },
                        onToggleFavorite = { productId ->
                            viewModel.toggleProductFavorite(productId)
                        }
                    )
                }

                // 2. TAB PESANAN
                composable(Routes.PESANAN) {
                    OrdersTabScreen(
                        uiState = uiState,
                        onOpenOrderDetail = { order ->
                            navController.navigate(Routes.orderDetail(order.id))
                        },
                        onOpenRating = { order ->
                            navController.navigate(Routes.orderRating(order.id))
                        },
                        onExploreHome = { switchTab(MainTab.BERANDA) }
                    )
                }

                // 3. TAB LIVE (posisi ke-3, UI/UX persis TikTok Live)
                composable(Routes.LIVE) {
                    LiveTabScreen(
                        uiState = uiState,
                        onToggleFavorite = { viewModel.toggleProductFavorite(it) },
                        onInitCheckout = { viewModel.initCheckoutForProduct(it) },
                        onSelectSlot = { viewModel.selectPickupSlot(it) },
                        onSelectPayment = { viewModel.selectPaymentMethod(it) },
                        onPlaceLiveOrder = { product, onCreated ->
                            viewModel.placeOrder(product, fromLive = true, onOrderCreated = onCreated)
                        },
                        onSendComment = { text, productId -> viewModel.sendLiveComment(text, productId) },
                        onNavigateToOrders = { switchTab(MainTab.PESANAN) }
                    )
                }

                // 4. TAB AKUN
                composable(Routes.AKUN) {
                    AccountTabScreen(
                        uiState = uiState,
                        onOpenWallet = { navController.navigate(Routes.WALLET) },
                        onOpenOrdersTab = { switchTab(MainTab.PESANAN) },
                        onOpenVouchers = { navController.navigate(Routes.VOUCHERS) },
                        onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                        onOpenViewHistory = { navController.navigate(Routes.VIEW_HISTORY) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onOpenMerchantScanQr = { navController.navigate(Routes.MERCHANT_SCAN_QR) },
                        onOpenMerchantDashboard = { navController.navigate(Routes.MERCHANT) }
                    )
                }

                // DETAIL PRODUK -> CHECKOUT -> PEMBAYARAN -> STATUS BERHASIL
                composable(
                    route = Routes.PRODUCT_DETAIL,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStack ->
                    val productId = Uri.decode(backStack.arguments?.getString("productId").orEmpty())
                    val product = uiState.products.find { it.id == productId }
                    if (product != null) {
                        ProductDetailScreen(
                            product = product,
                            reviews = uiState.productReviews.filter { it.productId == product.id },
                            onBack = { navController.popBackStack() },
                            onToggleFavorite = { viewModel.toggleProductFavorite(product.id) },
                            onBuyNow = {
                                viewModel.initCheckoutForProduct(product)
                                navController.navigate(Routes.checkout(product.id))
                            }
                        )
                    }
                }

                composable(
                    route = Routes.CHECKOUT,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStack ->
                    val productId = Uri.decode(backStack.arguments?.getString("productId").orEmpty())
                    val product = uiState.products.find { it.id == productId }
                    if (product != null) {
                        CheckoutPickupScreen(
                            product = product,
                            selectedSlot = uiState.selectedPickupSlot,
                            onSelectSlot = { viewModel.selectPickupSlot(it) },
                            onBack = { navController.popBackStack() },
                            onProceedToPayment = { navController.navigate(Routes.payment(product.id)) }
                        )
                    }
                }

                composable(
                    route = Routes.PAYMENT,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStack ->
                    val productId = Uri.decode(backStack.arguments?.getString("productId").orEmpty())
                    val product = uiState.products.find { it.id == productId }
                    if (product != null) {
                        PaymentScreen(
                            product = product,
                            pickupSlot = uiState.selectedPickupSlot,
                            walletBalanceRp = uiState.userPrefs.walletBalanceRp,
                            selectedMethod = uiState.selectedPaymentMethod,
                            onSelectMethod = { viewModel.selectPaymentMethod(it) },
                            onBack = { navController.popBackStack() },
                            onConfirmPay = {
                                viewModel.placeOrder(product, fromLive = false) { createdOrder ->
                                    navController.navigate(Routes.orderSuccess(createdOrder.id)) {
                                        popUpTo(Routes.BERANDA)
                                    }
                                }
                            }
                        )
                    }
                }

                composable(
                    route = Routes.ORDER_SUCCESS,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStack ->
                    val orderId = Uri.decode(backStack.arguments?.getString("orderId").orEmpty())
                    val order = uiState.orders.find { it.id == orderId }
                    if (order != null) {
                        OrderSuccessScreen(
                            order = order,
                            onContactStore = {
                                contactStoreInfo = order.storeName to order.storeAddress
                            },
                            onViewOrders = { switchTab(MainTab.PESANAN) },
                            onBackHome = { switchTab(MainTab.BERANDA) }
                        )
                    }
                }

                // PESANAN FLOW (Layar 2 - Layar 5)
                composable(
                    route = Routes.ORDER_DETAIL,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStack ->
                    val orderId = Uri.decode(backStack.arguments?.getString("orderId").orEmpty())
                    val order = uiState.orders.find { it.id == orderId }
                    if (order != null) {
                        OrderDetailScreen(
                            order = order,
                            onBack = { navController.popBackStack() },
                            onContactStore = {
                                contactStoreInfo = order.storeName to order.storeAddress
                            },
                            onOpenPickupStatus = {
                                navController.navigate(Routes.orderPickup(order.id))
                            },
                            onSimulateAutoCancel = {
                                viewModel.triggerAutoCancelExpiredOrder(order.id)
                            }
                        )
                    }
                }

                composable(
                    route = Routes.ORDER_PICKUP,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStack ->
                    val orderId = Uri.decode(backStack.arguments?.getString("orderId").orEmpty())
                    val order = uiState.orders.find { it.id == orderId }
                    if (order != null) {
                        OrderPickupStatusScreen(
                            order = order,
                            onBack = { navController.popBackStack() },
                            onAdvanceReady = { viewModel.advanceOrderToReady(order.id) },
                            onConfirmPickedUp = {
                                viewModel.confirmPickupCompleted(order.id)
                                navController.navigate(Routes.orderCompleted(order.id)) {
                                    popUpTo(Routes.PESANAN)
                                }
                            }
                        )
                    }
                }

                composable(
                    route = Routes.ORDER_COMPLETED,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStack ->
                    val orderId = Uri.decode(backStack.arguments?.getString("orderId").orEmpty())
                    val order = uiState.orders.find { it.id == orderId }
                    if (order != null) {
                        OrderCompletedScreen(
                            order = order,
                            onGoToRating = { navController.navigate(Routes.orderRating(order.id)) },
                            onBackToOrders = { switchTab(MainTab.PESANAN) }
                        )
                    }
                }

                composable(
                    route = Routes.ORDER_RATING,
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStack ->
                    val orderId = Uri.decode(backStack.arguments?.getString("orderId").orEmpty())
                    val order = uiState.orders.find { it.id == orderId }
                    if (order != null) {
                        OrderRatingScreen(
                            order = order,
                            onBack = { navController.popBackStack() },
                            onSubmitRating = { stars, comment ->
                                viewModel.submitRating(order.id, stars, comment)
                                switchTab(MainTab.PESANAN)
                            }
                        )
                    }
                }

                // AKUN SUB-SCREENS (Favorit, Riwayat Dilihat, Pengaturan, Dompet, Vouchers, Penjual)
                composable(Routes.FAVORITES) {
                    FavoritesScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onRemoveFavoriteStore = { viewModel.removeFavoriteStore(it) },
                        onToggleProductFavorite = { viewModel.toggleProductFavorite(it) },
                        onBuyProduct = { product ->
                            viewModel.initCheckoutForProduct(product)
                            navController.navigate(Routes.productDetail(product.id))
                        }
                    )
                }

                composable(Routes.VIEW_HISTORY) {
                    ViewHistoryScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onBuyAgain = { product ->
                            viewModel.initCheckoutForProduct(product)
                            navController.navigate(Routes.checkout(product.id))
                        }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onOpenNotificationSettings = {
                            navController.navigate(Routes.SETTINGS_NOTIFICATIONS)
                        },
                        onOpenLanguageSettings = {
                            navController.navigate(Routes.SETTINGS_LANGUAGE)
                        },
                        onOpenPrivacyPolicy = {
                            navController.navigate(Routes.SETTINGS_PRIVACY)
                        },
                        onOpenMerchantMode = { navController.navigate(Routes.MERCHANT) },
                        onLogout = {
                            viewModel.logout {
                                navController.navigate(Routes.REGISTER) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }

                composable(Routes.SETTINGS_NOTIFICATIONS) {
                    NotificationSettingsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onSaveSettings = { promo, order, live, wallet, hour ->
                            viewModel.updateNotificationSettings(
                                promoEnabled = promo,
                                orderEnabled = order,
                                liveEnabled = live,
                                walletEnabled = wallet,
                                hour = hour
                            )
                        }
                    )
                }

                composable(Routes.SETTINGS_LANGUAGE) {
                    LanguageSettingsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onApplyLanguage = { code, label ->
                            viewModel.updateAppLanguage(code, label)
                        }
                    )
                }

                composable(Routes.SETTINGS_PRIVACY) {
                    PrivacyPolicyScreen(
                        onBack = { navController.popBackStack() },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }

                composable(Routes.WALLET) {
                    WalletScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onTopUp = { viewModel.topUpSaldo(it) },
                        onRequestWithdraw = { amount, destination ->
                            viewModel.requestWithdraw(amount, destination)
                        },
                        onSimulateWithdrawComplete = { id ->
                            viewModel.markWithdrawCompleted(id)
                        }
                    )
                }

                composable(Routes.VOUCHERS) {
                    VouchersScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onUseVoucher = { switchTab(MainTab.BERANDA) }
                    )
                }

                composable(Routes.MERCHANT) {
                    MerchantScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onPublishListing = { cat, price, stock, window, uri ->
                            viewModel.publishMerchantListing(cat, price, stock, window, uri) {
                                switchTab(MainTab.BERANDA)
                            }
                        },
                        onAcceptOrder = { viewModel.acceptMerchantOrder(it) },
                        onRejectOrder = { viewModel.rejectMerchantOrder(it) },
                        onConfirmQrScanComplete = {
                            viewModel.confirmPickupCompleted(it)
                            viewModel.showMessage("QR berhasil di-scan! Pesanan selesai & sukses, dana diteruskan H+1.")
                        },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }

                composable(Routes.MERCHANT_SCAN_QR) {
                    MerchantQrScannerScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onConfirmQrScanComplete = { orderId ->
                            viewModel.confirmPickupCompleted(orderId)
                            viewModel.showMessage("QR berhasil di-scan! Pesanan $orderId selesai & sukses.")
                        },
                        onViewOrders = { switchTab(MainTab.PESANAN) },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }
            }
        }
    }

    // Contact Store Dialog (Hubungi Toko)
    contactStoreInfo?.let { (storeName, storeAddress) ->
        AlertDialog(
            onDismissRequest = { contactStoreInfo = null },
            title = { Text("Hubungi $storeName") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Alamat Ambil Sendiri: $storeAddress", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Catatan: Pembayaran hanya lewat aplikasi LUDES (tidak ada COD).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        contactStoreInfo = null
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:081234567890"))
                        runCatching { context.startActivity(dialIntent) }
                            .onFailure { viewModel.showMessage("Nomor warung: 0812-3456-7890") }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Icon(Icons.Filled.Phone, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Telepon Warung")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactStoreInfo = null }) { Text("Tutup") }
            }
        )
    }
}
