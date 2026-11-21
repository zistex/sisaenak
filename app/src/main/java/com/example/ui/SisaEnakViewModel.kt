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
    val text: String
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
    LiveComment("c1", "Ibu Ani", "masih ada rotinya kak?"),
    LiveComment("c2", "Rizky Mhs", "enak banget kemarin beli di sini!"),
    LiveComment("c3", "Teh Dewi", "aku otw ambil ya bu sari, jangan habis dulu"),
    LiveComment("c4", "Dina", "porsinya banyak beneran kak?"),
    LiveComment("c5", "Budi Dago", "harga 10rb dapat segini murah pisan")
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
        combine(snackbarMessage, liveComments) { snack, comments -> snack to comments }
    ) { data, (history, withdraws, reviews), prefs, inputs, (snack, comments) ->
        SisaEnakUiState(
            products = data.products,
            liveProducts = data.liveProducts,
            favoriteProducts = data.favProducts,
            favoriteStores = data.favStores,
            orders = data.orders,
            viewedHistory = history,
            withdrawRequests = withdraws,
            productReviews = reviews,
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

    fun updateLocation(location: String) {
        viewModelScope.launch {
            repository.updateLocation(location)
            snackbarMessage.value = "Lokasi diubah ke $location"
        }
    }

    fun updateNotificationHour(hour: String) {
        viewModelScope.launch {
            repository.updateNotificationHour(hour)
            snackbarMessage.value = "Pengingat harian diatur pukul $hour"
        }
    }

    fun topUpSaldo(amount: Int) {
        viewModelScope.launch {
            repository.topUpSaldo(amount)
            snackbarMessage.value = "Saldo Sisa Enak bertambah!"
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
                snackbarMessage.value = "Lewat 15 menit: Pesanan dibatalkan otomatis & dana kembali ke Saldo Sisa Enak."
            }
        }
    }

    fun submitRating(orderId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.submitOrderRating(orderId, rating, comment)
            snackbarMessage.value = "Terima kasih! Ulasan kamu sudah tersimpan."
        }
    }

    fun sendLiveComment(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val newComment = LiveComment(
            id = "c_${System.currentTimeMillis()}",
            author = "Siti Rahma",
            text = clean
        )
        liveComments.value = (liveComments.value + newComment).takeLast(12)
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

    fun clearSnackbar() {
        snackbarMessage.value = null
    }

    fun showMessage(message: String) {
        snackbarMessage.value = message
    }
}
