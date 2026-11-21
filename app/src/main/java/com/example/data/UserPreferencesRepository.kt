package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sisa_enak_prefs")

data class UserProfilePrefs(
    val userName: String = "Siti Rahma",
    val userEmail: String = "siti.rahma@gmail.com",
    val location: String = "Bandung - Dago",
    val userLat: Double = -6.8904,
    val userLng: Double = 107.6134,
    val walletBalanceRp: Int = 45000,
    val totalSavedRp: Int = 128000,
    val bagsRescued: Int = 12,
    val notificationHour: String = "19.00",
    val notifPromoEnabled: Boolean = true,
    val notifOrderEnabled: Boolean = true,
    val notifLiveEnabled: Boolean = true,
    val notifWalletEnabled: Boolean = true,
    val appLanguage: String = "id",
    val isLoggedIn: Boolean = false,
    val isEmailVerified: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_EMAIL_VERIFIED = booleanPreferencesKey("is_email_verified")
        val LOCATION = stringPreferencesKey("selected_location")
        val USER_LAT = doublePreferencesKey("user_lat")
        val USER_LNG = doublePreferencesKey("user_lng")
        val WALLET_BALANCE = intPreferencesKey("wallet_balance_rp")
        val TOTAL_SAVED = intPreferencesKey("total_saved_rp")
        val BAGS_RESCUED = intPreferencesKey("bags_rescued")
        val NOTIFICATION_HOUR = stringPreferencesKey("notification_hour")
        val NOTIF_PROMO_ENABLED = booleanPreferencesKey("notif_promo_enabled")
        val NOTIF_ORDER_ENABLED = booleanPreferencesKey("notif_order_enabled")
        val NOTIF_LIVE_ENABLED = booleanPreferencesKey("notif_live_enabled")
        val NOTIF_WALLET_ENABLED = booleanPreferencesKey("notif_wallet_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val userPrefs: Flow<UserProfilePrefs> = context.dataStore.data.map { prefs ->
        UserProfilePrefs(
            userName = prefs[Keys.USER_NAME] ?: "Siti Rahma",
            userEmail = prefs[Keys.USER_EMAIL] ?: "siti.rahma@gmail.com",
            location = prefs[Keys.LOCATION] ?: "Bandung - Dago",
            userLat = prefs[Keys.USER_LAT] ?: -6.8904,
            userLng = prefs[Keys.USER_LNG] ?: 107.6134,
            walletBalanceRp = prefs[Keys.WALLET_BALANCE] ?: 45000,
            totalSavedRp = prefs[Keys.TOTAL_SAVED] ?: 128000,
            bagsRescued = prefs[Keys.BAGS_RESCUED] ?: 12,
            notificationHour = prefs[Keys.NOTIFICATION_HOUR] ?: "19.00",
            notifPromoEnabled = prefs[Keys.NOTIF_PROMO_ENABLED] ?: true,
            notifOrderEnabled = prefs[Keys.NOTIF_ORDER_ENABLED] ?: true,
            notifLiveEnabled = prefs[Keys.NOTIF_LIVE_ENABLED] ?: true,
            notifWalletEnabled = prefs[Keys.NOTIF_WALLET_ENABLED] ?: true,
            appLanguage = prefs[Keys.APP_LANGUAGE] ?: "id",
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            isEmailVerified = prefs[Keys.IS_EMAIL_VERIFIED] ?: false
        )
    }

    suspend fun registerAndVerifyUser(name: String, email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name.trim()
            prefs[Keys.USER_EMAIL] = email.trim().lowercase()
            prefs[Keys.USER_PASSWORD] = password
            prefs[Keys.IS_EMAIL_VERIFIED] = true
            prefs[Keys.IS_LOGGED_IN] = true
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Pair<Boolean, String> {
        val prefs = context.dataStore.data.first()
        val cleanEmail = email.trim().lowercase()
        val savedEmail = prefs[Keys.USER_EMAIL]
        val savedPass = prefs[Keys.USER_PASSWORD]

        return when {
            savedEmail != null && savedPass != null &&
                cleanEmail == savedEmail && password == savedPass -> {
                context.dataStore.edit {
                    it[Keys.IS_LOGGED_IN] = true
                    it[Keys.IS_EMAIL_VERIFIED] = true
                }
                true to "Selamat datang kembali, ${prefs[Keys.USER_NAME] ?: "Sahabat LUDES"}!"
            }
            cleanEmail == "siti.rahma@gmail.com" && password == "123456" -> {
                loginDemoSiti()
                true to "Berhasil masuk sebagai Siti Rahma!"
            }
            else -> {
                false to "Email atau password tidak cocok. Silakan daftar atau cek lagi."
            }
        }
    }

    suspend fun loginDemoSiti() {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = "Siti Rahma"
            prefs[Keys.USER_EMAIL] = "siti.rahma@gmail.com"
            prefs[Keys.USER_PASSWORD] = "123456"
            prefs[Keys.IS_EMAIL_VERIFIED] = true
            prefs[Keys.IS_LOGGED_IN] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }

    suspend fun updateLocation(location: String, lat: Double, lng: Double) {
        context.dataStore.edit {
            it[Keys.LOCATION] = location
            it[Keys.USER_LAT] = lat
            it[Keys.USER_LNG] = lng
        }
    }

    suspend fun updateNotificationHour(hour: String) {
        context.dataStore.edit { it[Keys.NOTIFICATION_HOUR] = hour }
    }

    suspend fun updateNotificationSettings(
        promoEnabled: Boolean,
        orderEnabled: Boolean,
        liveEnabled: Boolean,
        walletEnabled: Boolean,
        hour: String
    ) {
        context.dataStore.edit {
            it[Keys.NOTIF_PROMO_ENABLED] = promoEnabled
            it[Keys.NOTIF_ORDER_ENABLED] = orderEnabled
            it[Keys.NOTIF_LIVE_ENABLED] = liveEnabled
            it[Keys.NOTIF_WALLET_ENABLED] = walletEnabled
            it[Keys.NOTIFICATION_HOUR] = hour
        }
    }

    suspend fun updateAppLanguage(languageCode: String) {
        context.dataStore.edit { it[Keys.APP_LANGUAGE] = languageCode }
    }

    suspend fun adjustWallet(deltaRp: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.WALLET_BALANCE] ?: 45000
            prefs[Keys.WALLET_BALANCE] = (current + deltaRp).coerceAtLeast(0)
        }
    }

    suspend fun recordBagRescued(savedRp: Int) {
        context.dataStore.edit { prefs ->
            val currentSaved = prefs[Keys.TOTAL_SAVED] ?: 128000
            val currentBags = prefs[Keys.BAGS_RESCUED] ?: 12
            prefs[Keys.TOTAL_SAVED] = currentSaved + savedRp.coerceAtLeast(0)
            prefs[Keys.BAGS_RESCUED] = currentBags + 1
        }
    }
}
