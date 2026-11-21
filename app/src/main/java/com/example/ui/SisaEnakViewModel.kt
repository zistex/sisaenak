package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FavoriteStoreEntity
import com.example.data.FoodCategory
import com.example.data.OrderEntity
import com.example.data.PaymentMethod
import com.example.data.ProductEntity
import com.example.data.ProductReviewEntity
import com.example.data.SisaEnakDatabase
import com.example.data.SisaEnakRepository
import com.example.data.UserPreferencesRepository
import com.example.data.UserProfilePrefs
import com.example.data.ViewedHistoryEntity
import com.example.data.VoucherItem
import com.example.data.WithdrawRequestEntity
import com.example.data.computeStoreDistanceText
import com.example.data.formatRupiah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LiveComment(
    val id: String,
    val author: String,
    val text: String,
    val isSelf: Boolean = false,
    val isHost: Boolean = false,
    val productId: String? = null,
    val timeLabel: String = "Baru saja"
)

enum class NotificationCategory(val label: String) {
    PESANAN("Pesanan"),
    LIVE("Live"),
    PROMO("Promo"),
    DOMPET("Dompet")
}

sealed class NotificationTarget {
    data class ProductDetail(val productId: String) : NotificationTarget()
    data object OrdersTab : NotificationTarget()
    data object LiveTab : NotificationTarget()
    data object Wallet : NotificationTarget()
    data object Vouchers : NotificationTarget()
    data object Search : NotificationTarget()
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeLabel: String,
    val category: NotificationCategory,
    val isRead: Boolean,
    val target: NotificationTarget,
    val isRecent: Boolean = true
)

data class SisaEnakUiState(
    val products: List<ProductEntity> = emptyList(),
    val liveProducts: List<ProductEntity> = emptyList(),
    val favoriteProducts: List<ProductEntity> = emptyList(),
    val favoriteStores: List<FavoriteStoreEntity> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val viewedHistory: List<ViewedHistoryEntity> = emptyList(),
    val withdrawRequests: List<WithdrawRequestEntity> = emptyList(),
    val productReviews: List<ProductReviewEntity> = emptyList(),
    val notifications: List<AppNotification> = emptyList(),
    val userPrefs: UserProfilePrefs = UserProfilePrefs(),
    val isAuthReady: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: FoodCategory? = null,
    val selectedPickupSlot: String = "",
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.QRIS,
    val lastCreatedOrderId: String? = null,
    val snackbarMessage: String? = null,
    val liveComments: List<LiveComment> = defaultLiveComments(),
    val vouchers: List<VoucherItem> = defaultVouchers()
) {
    val unreadNotificationCount: Int
        get() = notifications.count { !it.isRead }

    val totalFavoritesCount: Int
        get() = favoriteStores.size + favoriteProducts.size

    val hasLiveNow: Boolean
        get() = liveProducts.any { it.stock > 0 }

    val filteredProducts: List<ProductEntity>
        get() = products.filter { item ->
            val matchesCategory = selectedCategory == null || item.category == selectedCategory.name
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.storeName.lowercase().contains(q) ||
                item.category.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
}

private fun defaultLiveComments() = listOf(
    // Bakery Dago
    LiveComment("c_bd_0", "Bakery Dago", "Halo semuanya! Kantong kejutan roti malam ini isi croissant & roti manis fresh ya!", isHost = true, productId = "prod_bakery_dago", timeLabel = "5 mnt lalu"),
    LiveComment("c_bd_1", "Ibu Ani", "Kak, roti manisnya dapat berapa pcs per kantong?", productId = "prod_bakery_dago", timeLabel = "4 mnt lalu"),
    LiveComment("c_bd_2", "Bakery Dago", "Biasanya 4-5 pcs mix croissant keju & roti cokelat kak, masih empuk banget!", isHost = true, productId = "prod_bakery_dago", timeLabel = "3 mnt lalu"),
    LiveComment("c_bd_3", "Rizky Mhs", "Kemarin aku kebagian croissant almond + 3 roti sobek, worth it parah cuma 10rb 😍", productId = "prod_bakery_dago", timeLabel = "2 mnt lalu"),
    LiveComment("c_bd_4", "Budi Dago", "Otw ambil ke Jl. Dago No. 88 ya kak, jangan habis dulu!", productId = "prod_bakery_dago", timeLabel = "1 mnt lalu"),

    // Warung Bu Sari
    LiveComment("c_bs_0", "Warung Bu Sari", "Selamat malam! Paket nasi lauk lengkap + gorengan hangat siap diambil sampai jam 18.00 ya!", isHost = true, productId = "prod_warung_bu_sari", timeLabel = "6 mnt lalu"),
    LiveComment("c_bs_1", "Teh Dewi", "Bu Sari, hari ini lauknya ada ayam goreng atau rendang?", productId = "prod_warung_bu_sari", timeLabel = "4 mnt lalu"),
    LiveComment("c_bs_2", "Warung Bu Sari", "Ada ayam serundeng, orek tempe, sama bakwan jagung hangat neng!", isHost = true, productId = "prod_warung_bu_sari", timeLabel = "3 mnt lalu"),
    LiveComment("c_bs_3", "Dina", "Porsinya banyak beneran kak, bikin kenyang buat makan malam 👍", productId = "prod_warung_bu_sari", timeLabel = "1 mnt lalu"),

    // Martabak Simpang Dago
    LiveComment("c_ms_0", "Martabak Simpang", "Potongan martabak manis cokelat keju + kue basah baru dipacking nih kak!", isHost = true, productId = "prod_martabak_simpang", timeLabel = "5 mnt lalu"),
    LiveComment("c_ms_1", "Farhan", "Keju susunya masih banyak kan bang?", productId = "prod_martabak_simpang", timeLabel = "3 mnt lalu"),
    LiveComment("c_ms_2", "Salsa", "Barusan checkout 1 kantong, jaraknya cuma 600m dari kosan!", productId = "prod_martabak_simpang", timeLabel = "1 mnt lalu"),

    // Warung Kue Teh Rina
    LiveComment("c_tr_0", "Warung Kue Teh Rina", "Jajanan pasar campur lapis legit, dadar gulung & kue ku tanpa pengawet ya kak!", isHost = true, productId = "prod_kue_subuh_teh_rina", timeLabel = "5 mnt lalu"),
    LiveComment("c_tr_1", "Nisa", "Dadar gulungnya lembut banget, langganan tiap sore 😍", productId = "prod_kue_subuh_teh_rina", timeLabel = "2 mnt lalu")
)

private fun defaultVouchers() = listOf(
    VoucherItem(
        code = "HEMATMALAM",
        title = "Potongan Malam Pertama",
        description = "Khusus transaksi pertama ambil sendiri di toko",
        discountRp = 2000
    ),
    VoucherItem(
        code = "ANTISISA70",
        title = "Pejuang Anti Mubazir",
        description = "Berlaku untuk semua kategori Roti, Nasi, Jajanan, Buah",
        discountRp = 3000
    )
)

class SisaEnakViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SisaEnakRepository

    init {
        val db = SisaEnakDatabase.getInstance(application)
        val prefs = UserPreferencesRepository(application)
        repository = SisaEnakRepository(db, prefs)
        viewModelScope.launch {
            repository.initialize()
        }
    }

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<FoodCategory?>(null)
    private val selectedPickupSlot = MutableStateFlow("")
    private val selectedPaymentMethod = MutableStateFlow(PaymentMethod.QRIS)
    private val lastCreatedOrderId = MutableStateFlow<String?>(null)
    private val snackbarMessage = MutableStateFlow<String?>(null)
    private val liveComments = MutableStateFlow(defaultLiveComments())
    private val readNotificationIds = MutableStateFlow(
        setOf("notif_voucher_antisisa", "notif_wallet_info", "notif_reminder_evening")
    )

    private val dataFlow = combine(
        repository.products,
        repository.liveProducts,
        repository.favoriteProducts,
        repository.favoriteStores,
        repository.orders
    ) { products, liveProducts, favProducts, favStores, orders ->
        DataSnapshot(products, liveProducts, favProducts, favStores, orders)
    }

    private val uiInputsFlow = combine(
        searchQuery,
        selectedCategory,
        selectedPickupSlot,
        selectedPaymentMethod,
        lastCreatedOrderId
    ) { query, cat, slot, pay, lastOrder ->
        UiInputs(query, cat, slot, pay, lastOrder)
    }

    val uiState: StateFlow<SisaEnakUiState> = combine(
        dataFlow,
        combine(
            repository.viewedHistory,
            repository.withdrawRequests,
            repository.productReviews
        ) { h, w, r -> Triple(h, w, r) },
        repository.userPrefs,
        uiInputsFlow,
        combine(snackbarMessage, liveComments, readNotificationIds) { snack, comments, readIds ->
            Triple(snack, comments, readIds)
        }
    ) { data, (history, withdraws, reviews), prefs, inputs, (snack, comments, readIds) ->
        val builtNotifications = buildAppNotifications(
            orders = data.orders,
            withdrawRequests = withdraws,
            liveProducts = data.liveProducts,
            readIds = readIds
        )
        fun withUserDistance(list: List<ProductEntity>): List<ProductEntity> =
            list.map { p ->
                p.copy(
                    distanceText = computeStoreDistanceText(
                        productId = p.id,
                        storeAddress = p.storeAddress,
                        userLat = prefs.userLat,
                        userLng = prefs.userLng
                    )
                )
            }

        SisaEnakUiState(
            products = withUserDistance(data.products),
            liveProducts = withUserDistance(data.liveProducts),
            favoriteProducts = withUserDistance(data.favProducts),
            favoriteStores = data.favStores,
            orders = data.orders,
            viewedHistory = history,
            withdrawRequests = withdraws,
            productReviews = reviews,
            notifications = builtNotifications,
            userPrefs = prefs,
            isAuthReady = true,
            searchQuery = inputs.query,
            selectedCategory = inputs.category,
            selectedPickupSlot = inputs.pickupSlot,
            selectedPaymentMethod = inputs.paymentMethod,
            lastCreatedOrderId = inputs.lastOrderId,
            snackbarMessage = snack,
            liveComments = comments
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SisaEnakUiState()
    )

    private fun buildAppNotifications(
        orders: List<OrderEntity>,
        withdrawRequests: List<WithdrawRequestEntity>,
        liveProducts: List<ProductEntity>,
        readIds: Set<String>
    ): List<AppNotification> {
        val list = mutableListOf<AppNotification>()

        orders.filter { it.status == com.example.data.OrderStatus.BELUM_DIAMBIL.name }
            .take(2)
            .forEach { order ->
                val id = "notif_order_${order.id}"
                list += AppNotification(
                    id = id,
                    title = "Siap diambil: ${order.productTitle}",
                    message = "Tunjukkan kode QR ${order.id} di ${order.storeName} pada jam ${order.pickupSlot}.",
                    timeLabel = "Baru saja",
                    category = NotificationCategory.PESANAN,
                    isRead = readIds.contains(id),
                    target = NotificationTarget.OrdersTab,
                    isRecent = true
                )
            }

        withdrawRequests.take(1).forEach { w ->
            val id = "notif_wd_${w.id}"
            list += AppNotification(
                id = id,
                title = "Penarikan Saldo ${formatRupiah(w.amountRp)}",
                message = "Status: ${w.status} menuju ${w.destinationLabel}.",
                timeLabel = "Hari ini",
                category = NotificationCategory.DOMPET,
                isRead = readIds.contains(id),
                target = NotificationTarget.Wallet,
                isRecent = true
            )
        }

        val liveCount = liveProducts.count { it.stock > 0 }.coerceAtLeast(1)
        list += AppNotification(
            id = "notif_live_busari",
            title = "Warung Bu Sari & $liveCount toko sedang LIVE",
            message = "1.2rb penonton sedang berebut kantong hemat Rp10.000. Tonton langsung & amankan stok!",
            timeLabel = "5 mnt lalu",
            category = NotificationCategory.LIVE,
            isRead = readIds.contains("notif_live_busari"),
            target = NotificationTarget.LiveTab,
            isRecent = true
        )

        list += AppNotification(
            id = "notif_promo_bakery",
            title = "Bakery Dago — Diskon hingga 60%",
            message = "Tersisa kantong roti croissant & pastry fresh hari ini. Ambil sendiri sebelum jam 21.00!",
            timeLabel = "18 mnt lalu",
            category = NotificationCategory.PROMO,
            isRead = readIds.contains("notif_promo_bakery"),
            target = NotificationTarget.ProductDetail("prod_bakery_dago"),
            isRecent = true
        )

        list += AppNotification(
            id = "notif_voucher_antisisa",
            title = "Voucher ANTISISA70 aktif untukmu",
            message = "Nikmati potongan Rp3.000 untuk kategori Roti, Nasi, Jajanan & Buah tanpa minimum ongkir.",
            timeLabel = "2 jam lalu",
            category = NotificationCategory.PROMO,
            isRead = readIds.contains("notif_voucher_antisisa"),
            target = NotificationTarget.Vouchers,
            isRecent = false
        )

        list += AppNotification(
            id = "notif_wallet_info",
            title = "Saldo Dompet LUDES siap ditarik",
            message = "Kelola saldo refund & ajukan tarik saldo ke rekening bank atau e-wallet kapan saja.",
            timeLabel = "Hari ini",
            category = NotificationCategory.DOMPET,
            isRead = readIds.contains("notif_wallet_info"),
            target = NotificationTarget.Wallet,
            isRecent = false
        )

        list += AppNotification(
            id = "notif_reminder_evening",
            title = "Waktunya berburu makanan enak hemat sebelum ludes!",
            message = "Cek warung terdekat di Bandung dengan filter jarak & harga di menu Pencarian.",
            timeLabel = "Kemarin",
            category = NotificationCategory.PROMO,
            isRead = readIds.contains("notif_reminder_evening"),
            target = NotificationTarget.Search,
            isRecent = false
        )

        return list
    }

    private data class DataSnapshot(
        val products: List<ProductEntity>,
        val liveProducts: List<ProductEntity>,
        val favProducts: List<ProductEntity>,
        val favStores: List<FavoriteStoreEntity>,
        val orders: List<OrderEntity>
    )

    private data class UiInputs(
        val query: String,
        val category: FoodCategory?,
        val pickupSlot: String,
        val paymentMethod: PaymentMethod,
        val lastOrderId: String?
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleCategory(category: FoodCategory) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    fun selectPickupSlot(slot: String) {
        selectedPickupSlot.value = slot
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod.value = method
    }

    fun initCheckoutForProduct(product: ProductEntity) {
        val slots = product.pickupSlotsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        selectedPickupSlot.value = slots.firstOrNull().orEmpty()
        selectedPaymentMethod.value = PaymentMethod.QRIS
    }

    fun toggleProductFavorite(productId: String) {
        viewModelScope.launch {
            repository.toggleProductFavorite(productId)
        }
    }

    fun removeFavoriteStore(storeId: String) {
        viewModelScope.launch {
            repository.removeFavoriteStore(storeId)
            snackbarMessage.value = "Warung dihapus dari Favorit"
        }
    }

    fun recordProductViewed(productId: String) {
        viewModelScope.launch {
            repository.recordProductViewed(productId)
        }
    }

    fun registerAndVerifyUser(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.registerAndVerifyUser(name, email, password)
            snackbarMessage.value = "Selamat datang, ${name.trim()}! Email berhasil diverifikasi."
            onSuccess()
        }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.loginWithEmail(email, password)
            if (ok) {
                snackbarMessage.value = msg
                onSuccess()
            } else {
                onError(msg)
            }
        }
    }

    fun loginDemoSiti(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.loginDemoSiti()
            snackbarMessage.value = "Berhasil masuk sebagai Siti Rahma!"
            onSuccess()
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            snackbarMessage.value = "Kamu telah keluar dari akun."
            onLoggedOut()
        }
    }

    fun updateLocation(location: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            repository.updateLocation(location, lat, lng)
            snackbarMessage.value = "Acuan lokasi diatur ke $location"
        }
    }

    fun updateNotificationHour(hour: String) {
        viewModelScope.launch {
            repository.updateNotificationHour(hour)
            snackbarMessage.value = "Pengingat harian diatur pukul $hour"
        }
    }

    fun updateNotificationSettings(
        promoEnabled: Boolean,
        orderEnabled: Boolean,
        liveEnabled: Boolean,
        walletEnabled: Boolean,
        hour: String
    ) {
        viewModelScope.launch {
            repository.updateNotificationSettings(
                promoEnabled = promoEnabled,
                orderEnabled = orderEnabled,
                liveEnabled = liveEnabled,
                walletEnabled = walletEnabled,
                hour = hour
            )
            snackbarMessage.value = "Preferensi notifikasi berhasil disimpan"
        }
    }

    fun updateAppLanguage(languageCode: String, label: String) {
        viewModelScope.launch {
            repository.updateAppLanguage(languageCode)
            snackbarMessage.value = "Bahasa aplikasi diatur ke $label"
        }
    }

    fun topUpSaldo(amount: Int) {
        viewModelScope.launch {
            repository.topUpSaldo(amount)
            snackbarMessage.value = "Saldo LUDES bertambah!"
        }
    }

    fun requestWithdraw(amountRp: Int, destinationLabel: String) {
        viewModelScope.launch {
            val req = repository.requestWithdraw(amountRp, destinationLabel)
            if (req != null) {
                snackbarMessage.value = "Permintaan tarik saldo ${formatRupiah(amountRp)} (${req.id}) sedang diproses!"
            } else {
                snackbarMessage.value = "Saldo tidak mencukupi (minimal penarikan Rp10.000)."
            }
        }
    }

    fun markWithdrawCompleted(id: String) {
        viewModelScope.launch {
            repository.markWithdrawCompleted(id)
            snackbarMessage.value = "Penarikan $id berhasil dicairkan!"
        }
    }

    fun placeOrder(
        product: ProductEntity,
        fromLive: Boolean,
        onOrderCreated: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val slot = selectedPickupSlot.value.ifBlank {
                product.pickupSlotsCsv.split(",").firstOrNull()?.trim().orEmpty()
            }
            val order = repository.placePickupOrder(
                product = product,
                pickupSlot = slot,
                paymentMethod = selectedPaymentMethod.value,
                fromLive = fromLive
            )
            lastCreatedOrderId.value = order.id
            onOrderCreated(order)
        }
    }

    fun advanceOrderToReady(orderId: String) {
        viewModelScope.launch {
            repository.advanceOrderToReady(orderId)
        }
    }

    fun confirmPickupCompleted(orderId: String) {
        viewModelScope.launch {
            repository.confirmPickupCompleted(orderId)
        }
    }

    fun triggerAutoCancelExpiredOrder(orderId: String) {
        viewModelScope.launch {
            val refund = repository.triggerAutoCancelExpiredOrder(orderId)
            if (refund > 0) {
                snackbarMessage.value = "Lewat 15 menit: Pesanan dibatalkan otomatis & dana kembali ke Saldo LUDES."
            }
        }
    }

    fun submitRating(orderId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.submitOrderRating(orderId, rating, comment)
            snackbarMessage.value = "Terima kasih! Ulasan kamu sudah tersimpan."
        }
    }

    fun sendLiveComment(text: String, productId: String? = null) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val currentUserName = uiState.value.userPrefs.userName.trim().ifBlank { "Siti Rahma" }
        val newComment = LiveComment(
            id = "c_${System.nanoTime()}",
            author = currentUserName,
            text = clean,
            isSelf = true,
            productId = productId,
            timeLabel = "Baru saja"
        )
        liveComments.value = (liveComments.value + newComment).takeLast(60)
    }

    fun publishMerchantListing(
        category: FoodCategory,
        price: Int,
        stock: Int,
        pickupWindow: String,
        customImageUri: String?,
        onPublished: () -> Unit
    ) {
        viewModelScope.launch {
            repository.publishMerchantListing(
                category = category,
                price = price,
                stock = stock,
                pickupWindow = pickupWindow,
                customImageUri = customImageUri
            )
            snackbarMessage.value = "Kantong berhasil ditayangkan di Beranda!"
            onPublished()
        }
    }

    fun acceptMerchantOrder(orderId: String) {
        viewModelScope.launch {
            repository.acceptMerchantOrder(orderId)
            snackbarMessage.value = "Pesanan diterima! Siapkan kantong untuk diambil pembeli."
        }
    }

    fun rejectMerchantOrder(orderId: String) {
        viewModelScope.launch {
            repository.rejectMerchantOrder(orderId)
            snackbarMessage.value = "Pesanan ditolak & dana dikembalikan ke Saldo pembeli."
        }
    }

    fun markNotificationRead(id: String) {
        readNotificationIds.value = readNotificationIds.value + id
    }

    fun markAllNotificationsRead() {
        val allIds = uiState.value.notifications.map { it.id }.toSet()
        readNotificationIds.value = readNotificationIds.value + allIds
        snackbarMessage.value = "Semua notifikasi ditandai sudah dibaca"
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    fun showMessage(message: String) {
        snackbarMessage.value = message
    }
}
