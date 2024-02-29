package com.txurdinaga.proyectofinaldam.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object EncryptedPrefsUtil {
    private lateinit var sharedPreferences: SharedPreferences
    // Singleton pattern
    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context.applicationContext,
                "secret_shared_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getToken(): String {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InVua2luYS1zZXJ2ZXIiLCJhdWQiOiJ1bmtpbmEtY2xpZW50IiwidXNlcklkIjoiODAzYzZkYjAtODBkZS00ZjI1LThmNmItMDlhMGFiNjBlNzg0IiwiaXNBZG1pbiI6dHJ1ZX0.Sr0N8mhKljpaoN1a_9sNAQvYe6EXPBppGC3k_xAgDoQ"
    }
}