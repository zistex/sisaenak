package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SisaEnakRepository(
    private val database: SisaEnakDatabase,
    private val prefsRepo: UserPreferencesRepository
) {
    private val productDao = database.productDao()
    private val favoriteStoreDao = database.favoriteStoreDao()
    private val orderDao = database.orderDao()
    private val viewedHistoryDao = database.viewedHistoryDao()
    private val withdrawDao = database.withdrawDao()
    private val productReviewDao = database.productReviewDao()

    val products: Flow<List<ProductEntity>> = productDao.observeAllProducts()
    val liveProducts: Flow<List<ProductEntity>> = productDao.observeLiveProducts()
    val favoriteProducts: Flow<List<ProductEntity>> = productDao.observeFavoriteProducts()
    val favoriteStores: Flow<List<FavoriteStoreEntity>> = favoriteStoreDao.observeFavoriteStores()
    val orders: Flow<List<OrderEntity>> = orderDao.observeAllOrders()
    val viewedHistory: Flow<List<ViewedHistoryEntity>> = viewedHistoryDao.observeHistory()
    val withdrawRequests: Flow<List<WithdrawRequestEntity>> = withdrawDao.observeAllWithdraws()
    val productReviews: Flow<List<ProductReviewEntity>> = productReviewDao.observeAllReviews()
    val userPrefs: Flow<UserProfilePrefs> = prefsRepo.userPrefs

    suspend fun initialize() {
        SisaEnakDatabase.ensureSeeded(database)
    }

    suspend fun registerAndVerifyUser(name: String, email: String, password: String) {
        prefsRepo.registerAndVerifyUser(name, email, password)
    }

    suspend fun loginWithEmail(email: String, password: String): Pair<Boolean, String> {
        return prefsRepo.loginWithEmail(email, password)
    }

    suspend fun loginDemoSiti() {
        prefsRepo.loginDemoSiti()
    }

    suspend fun logout() {
        prefsRepo.logout()
    }

    suspend fun toggleProductFavorite(productId: String) {
        val product = productDao.getProductById(productId) ?: return
        val newFav = !product.isFavorite
        val delta = if (newFav) 1 else -1
        productDao.updateFavoriteWithCount(productId, newFav, delta)
    }

    suspend fun removeFavoriteStore(storeId: String) {
        favoriteStoreDao.setStoreFavorite(storeId, false)
    }

    suspend fun recordProductViewed(productId: String) {
        productDao.incrementViewCount(productId)
        viewedHistoryDao.upsert(
            ViewedHistoryEntity(
                productId = productId,
                viewedLabel = "Baru saja dilihat",
                viewedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateLocation(location: String, lat: Double, lng: Double) {
        prefsRepo.updateLocation(location, lat, lng)
    }

    suspend fun updateNotificationHour(hour: String) {
        prefsRepo.updateNotificationHour(hour)
    }

    suspend fun updateNotificationSettings(
        promoEnabled: Boolean,
        orderEnabled: Boolean,
        liveEnabled: Boolean,
        walletEnabled: Boolean,
        hour: String
    ) {
        prefsRepo.updateNotificationSettings(
            promoEnabled = promoEnabled,
            orderEnabled = orderEnabled,
            liveEnabled = liveEnabled,
            walletEnabled = walletEnabled,
            hour = hour
        )
    }

    suspend fun updateAppLanguage(languageCode: String) {
        prefsRepo.updateAppLanguage(languageCode)
    }

    suspend fun topUpSaldo(amount: Int) {
        prefsRepo.adjustWallet(amount)
    }

    suspend fun requestWithdraw(amountRp: Int, destinationLabel: String): WithdrawRequestEntity? {
        val currentBalance = prefsRepo.userPrefs.first().walletBalanceRp
        if (amountRp < 10000 || currentBalance < amountRp) return null
        prefsRepo.adjustWallet(-amountRp)
        val req = WithdrawRequestEntity(
            id = "WD-${(1000..9999).random()}",
            amountRp = amountRp,
            destinationLabel = destinationLabel,
            status = "Diproses",
            createdAtMillis = System.currentTimeMillis()
        )
        withdrawDao.upsert(req)
        return req
    }

    suspend fun markWithdrawCompleted(id: String) {
        withdrawDao.updateStatus(id, "Berhasil Dicairkan")
    }

    suspend fun placePickupOrder(
        product: ProductEntity,
        pickupSlot: String,
        paymentMethod: PaymentMethod,
        fromLive: Boolean
    ): OrderEntity {
        val serviceFee = 1000
        val total = product.discountPrice + serviceFee

        if (paymentMethod == PaymentMethod.SALDO_SISA_ENAK) {
            val currentWallet = prefsRepo.userPrefs.first().walletBalanceRp
            if (currentWallet >= total) {
                prefsRepo.adjustWallet(-total)
            }
        }

        productDao.adjustStock(product.id, -1)

        val randomCode = (100000..999999).random()
        val orderId = if (product.id == "prod_bakery_dago" && !fromLive) {
            "#SE102938"
        } else {
            "#SE$randomCode"
        }

        val uniqueOrder = if (orderDao.getOrderById(orderId) != null) {
            "#SE${(100000..999999).random()}"
        } else {
            orderId
        }

        val buyerName = prefsRepo.userPrefs.first().userName.ifBlank { "Siti Rahma" }

        val order = OrderEntity(
            id = uniqueOrder,
            productId = product.id,
            productTitle = product.title,
            storeName = product.storeName,
            storeAddress = product.storeAddress,
            distanceText = product.distanceText,
            imageKey = product.imageKey,
            originalPrice = product.originalPrice,
            bagPrice = product.discountPrice,
            serviceFee = serviceFee,
            totalPaid = total,
            paymentMethod = paymentMethod.label,
            pickupSlot = pickupSlot,
            status = OrderStatus.BELUM_DIAMBIL.name,
            isReadyForPickup = false,
            merchantAccepted = false,
            buyerName = buyerName,
            createdAtMillis = System.currentTimeMillis(),
            fromLive = fromLive
        )
        orderDao.upsert(order)
        return order
    }

    suspend fun advanceOrderToReady(orderId: String) {
        orderDao.setMerchantAccepted(orderId, accepted = true, ready = true)
    }

    suspend fun confirmPickupCompleted(orderId: String) {
        val order = orderDao.getOrderById(orderId) ?: return
        if (order.status != OrderStatus.SUDAH_DIAMBIL.name) {
            orderDao.updateOrderStatus(orderId, OrderStatus.SUDAH_DIAMBIL.name, ready = true)
            val saved = (order.originalPrice - order.bagPrice).coerceAtLeast(5000)
            prefsRepo.recordBagRescued(saved)
        }
    }

    /**
     * Aturan Batal Otomatis:
     * Jika user tidak ambil sampai 15 menit lewat dari slot jam yang dipilih,
     * pesanan otomatis batal, uang kembali jadi Saldo LUDES, kantong muncul lagi di Beranda.
     */
    suspend fun triggerAutoCancelExpiredOrder(orderId: String): Int {
        val order = orderDao.getOrderById(orderId) ?: return 0
        if (order.status != OrderStatus.BELUM_DIAMBIL.name) return 0
        orderDao.updateOrderStatus(orderId, OrderStatus.BATAL.name, ready = false)
        productDao.adjustStock(order.productId, +1)
        prefsRepo.adjustWallet(order.totalPaid)
        return order.totalPaid
    }

    suspend fun submitOrderRating(orderId: String, rating: Int, comment: String) {
        val cleanComment = comment.trim()
        orderDao.submitRating(orderId, rating, cleanComment)
        val order = orderDao.getOrderById(orderId) ?: return
        productReviewDao.upsert(
            ProductReviewEntity(
                id = "rev_order_$orderId",
                productId = order.productId,
                reviewerName = order.buyerName.ifBlank { "Pembeli LUDES" },
                rating = rating.coerceIn(1, 5),
                comment = cleanComment.ifEmpty { "Makanan masih segar dan enak banget!" },
                timeLabel = "Baru saja",
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun publishMerchantListing(
        category: FoodCategory,
        price: Int,
        stock: Int,
        pickupWindow: String,
        customImageUri: String?
    ): ProductEntity {
        val imageKey = when (category) {
            FoodCategory.ROTI -> "bakery"
            FoodCategory.NASI -> "nasi"
            FoodCategory.JAJANAN -> "jajanan"
            FoodCategory.BUAH -> "buah"
            FoodCategory.LAINNYA -> "lainnya"
        }
        val title = when (category) {
            FoodCategory.ROTI -> "Kantong Kejutan Roti Warung"
            FoodCategory.NASI -> "Paket Nasi & Lauk Hemat Malam"
            FoodCategory.JAJANAN -> "Kantong Jajanan & Kue Segar"
            FoodCategory.BUAH -> "Kotak Buah Potong Segar"
            FoodCategory.LAINNYA -> "Paket Kuliner & Minuman Hemat"
        }
        val slots = when (pickupWindow) {
            "18.00-20.00" -> "18.00 - 18.30,18.30 - 19.00,19.00 - 19.30"
            "20.00-22.00" -> "20.00 - 20.30,20.30 - 21.00,21.00 - 21.30"
            else -> "19.00 - 19.30,19.30 - 20.00,20.00 - 20.30"
        }
        val item = ProductEntity(
            id = "merchant_${System.currentTimeMillis()}",
            title = title,
            storeName = "Warung Berkah Ibu Siti",
            storeAddress = "Jl. Dago Pojok No. 14",
            distanceText = "0.3 km",
            category = category.name,
            originalPrice = price * 2,
            discountPrice = price,
            stock = stock,
            pickupWindow = pickupWindow,
            pickupSlotsCsv = slots,
            rating = 5.0,
            description = "Makanan sisa layak konsumsi hari ini dari Warung Berkah Ibu Siti. Segar, bersih, hanya ambil sendiri di warung.",
            imageKey = imageKey,
            customImageUri = customImageUri,
            isFavorite = false,
            isLive = false,
            isMerchantPublished = true
        )
        productDao.upsert(item)
        return item
    }

    suspend fun acceptMerchantOrder(orderId: String) {
        orderDao.setMerchantAccepted(orderId, accepted = true, ready = true)
    }

    suspend fun rejectMerchantOrder(orderId: String) {
        val order = orderDao.getOrderById(orderId) ?: return
        if (order.status == OrderStatus.BELUM_DIAMBIL.name) {
            orderDao.updateOrderStatus(orderId, OrderStatus.BATAL.name, ready = false)
            productDao.adjustStock(order.productId, +1)
            prefsRepo.adjustWallet(order.totalPaid)
        }
    }
}
