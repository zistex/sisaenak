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
    SALDO_SISA_ENAK("Saldo LUDES", "Saldo refund & dompet aplikasi")
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

data class MapAreaPreset(
    val label: String,
    val shortName: String,
    val streetHint: String,
    val lat: Double,
    val lng: Double
)

val bandungAreaPresets = listOf(
    MapAreaPreset("Bandung - Dago", "Dago", "Jl. Ir. H. Juanda / Dago", -6.8904, 107.6134),
    MapAreaPreset("Bandung - Dipatiukur", "Dipatiukur", "Jl. Dipatiukur & Sekitar ITB", -6.8935, 107.6176),
    MapAreaPreset("Bandung - Riau", "Riau", "Jl. LLRE Martadinata (Riau)", -6.9062, 107.6198),
    MapAreaPreset("Bandung - Cihampelas", "Cihampelas", "Jl. Cihampelas & Tamansari", -6.8958, 107.6038),
    MapAreaPreset("Bandung - Cisitu", "Cisitu", "Jl. Cisitu & Siliwangi", -6.8834, 107.6085),
    MapAreaPreset("Bandung - Buah Batu", "Buah Batu", "Jl. Buah Batu Tengah", -6.9338, 107.6245),
    MapAreaPreset("Bandung - Braga", "Braga", "Jl. Braga & Balai Kota", -6.9175, 107.6094)
)

fun storeCoordinatesForProduct(productId: String, storeAddress: String): Pair<Double, Double> {
    return when (productId) {
        "prod_bakery_dago" -> -6.8904 to 107.6134
        "prod_warung_bu_sari" -> -6.8891 to 107.6121
        "prod_warteg_bahari" -> -6.8962 to 107.6129
        "prod_martabak_simpang" -> -6.8935 to 107.6176
        "prod_buah_segar" -> -6.8878 to 107.6194
        "prod_roti_tawar_sore" -> -6.8834 to 107.6085
        "prod_kue_subuh_teh_rina" -> -6.8855 to 107.6052
        "prod_salad_buah_dago" -> -6.8958 to 107.6038
        else -> {
            val addr = storeAddress.lowercase()
            when {
                "dipatiukur" in addr -> -6.8935 to 107.6176
                "riau" in addr || "martadinata" in addr -> -6.9062 to 107.6198
                "cihampelas" in addr -> -6.8958 to 107.6038
                "cisitu" in addr || "siliwangi" in addr -> -6.8834 to 107.6085
                "buah batu" in addr -> -6.9338 to 107.6245
                "braga" in addr -> -6.9175 to 107.6094
                else -> -6.8915 to 107.6142
            }
        }
    }
}

fun haversineDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
        kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadiusKm * c
}

fun formatDistanceFromKm(km: Double): String {
    val safeKm = km.coerceAtLeast(0.05)
    return if (safeKm < 0.95) {
        val meters = ((safeKm * 1000.0) / 50.0).let { kotlin.math.round(it) * 50 }.toInt().coerceAtLeast(50)
        "${meters}m"
    } else {
        String.format(Locale.US, "%.1f km", safeKm)
    }
}

fun computeStoreDistanceText(
    productId: String,
    storeAddress: String,
    userLat: Double,
    userLng: Double
): String {
    val (sLat, sLng) = storeCoordinatesForProduct(productId, storeAddress)
    val km = haversineDistanceKm(userLat, userLng, sLat, sLng)
    return formatDistanceFromKm(km)
}

fun nearestBandungAreaLabel(lat: Double, lng: Double): String {
    val closest = bandungAreaPresets.minByOrNull { preset ->
        haversineDistanceKm(lat, lng, preset.lat, preset.lng)
    } ?: return "Bandung - Dago"
    val distKm = haversineDistanceKm(lat, lng, closest.lat, closest.lng)
    return if (distKm <= 3.5) {
        closest.label
    } else {
        String.format(Locale.US, "Titik Peta (%.3f, %.3f)", lat, lng)
    }
}
