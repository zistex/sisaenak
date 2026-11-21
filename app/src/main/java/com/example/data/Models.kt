package com.example.data

import androidx.annotation.DrawableRes
import com.example.R
import java.text.NumberFormat
import java.util.Locale

enum class FoodCategory(val label: String) {
    ROTI("Roti"),
    NASI("Nasi"),
    JAJANAN("Jajanan"),
    BUAH("Buah"),
    LAINNYA("Lainnya")
}

enum class OrderStatus(val label: String) {
    BELUM_DIAMBIL("Belum Diambil"),
    SUDAH_DIAMBIL("Sudah Diambil"),
    BATAL("Batal")
}

enum class StoreStatusType {
    OPEN_LIMITED,
    OPEN_MANY,
    CLOSED
}

enum class PaymentMethod(val label: String, val subtitle: String) {
    QRIS("QRIS", "Scan QR semua e-wallet & m-banking"),
    GOPAY("GoPay", "Saldo Rp25.000"),
    OVO("OVO", "Saldo Rp12.000"),
    DANA("DANA", "Bayar instan tanpa biaya tambahan"),
    TRANSFER_BANK("Transfer Bank", "BCA / BRI / Mandiri / BNI"),
    SALDO_SISA_ENAK("Saldo Sisa Enak", "Saldo refund & dompet aplikasi")
}

data class VoucherItem(
    val code: String,
    val title: String,
    val description: String,
    val discountRp: Int
)

fun formatRupiah(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp${formatter.format(amount)}"
}

fun formatRupiahShort(amount: Int): String {
    return if (amount >= 1000 && amount % 1000 == 0) {
        "${amount / 1000}rb"
    } else {
        formatRupiah(amount)
    }
}

fun formatCompactCount(count: Int): String {
    return if (count >= 1000) {
        val thousands = count / 1000.0
        String.format(Locale("id", "ID"), "%.1frb", thousands).replace(",0rb", "rb")
    } else {
        count.toString()
    }
}

fun parseDistanceKm(distanceText: String): Double {
    val clean = distanceText.trim().lowercase().replace(",", ".")
    return when {
        clean.endsWith("km") -> {
            clean.removeSuffix("km").trim().toDoubleOrNull() ?: 99.0
        }
        clean.endsWith("m") -> {
            val meters = clean.removeSuffix("m").trim().toDoubleOrNull() ?: 99000.0
            meters / 1000.0
        }
        else -> {
            val numeric = Regex("[0-9]+(\\.[0-9]+)?").find(clean)?.value?.toDoubleOrNull()
            numeric ?: 99.0
        }
    }
}

@DrawableRes
fun resolveFoodDrawable(imageKey: String): Int {
    return when (imageKey) {
        "bakery" -> R.drawable.img_food_bakery
        "nasi" -> R.drawable.img_food_nasi
        "jajanan" -> R.drawable.img_food_jajanan
        "buah" -> R.drawable.img_food_buah
        "lainnya" -> R.drawable.img_food_jajanan
        else -> R.drawable.img_food_bakery
    }
}

fun resolveProductGalleryKeys(
    primaryImageKey: String,
    customImageUri: String?,
    category: String
): List<String> {
    val categoryOrder = when (category) {
        FoodCategory.ROTI.name -> listOf("bakery", "jajanan", "buah")
        FoodCategory.NASI.name -> listOf("nasi", "jajanan", "buah")
        FoodCategory.JAJANAN.name -> listOf("jajanan", "bakery", "buah")
        FoodCategory.BUAH.name -> listOf("buah", "jajanan", "bakery")
        else -> listOf("jajanan", "nasi", "bakery")
    }
    val base = if (!customImageUri.isNullOrBlank()) {
        listOf("uri:$customImageUri") + categoryOrder
    } else {
        val normalizedPrimary = if (primaryImageKey == "lainnya") "jajanan" else primaryImageKey
        listOf(normalizedPrimary) + categoryOrder
    }
    return base.distinct().take(3)
}
