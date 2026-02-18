package com.example.patchtracker.nightscout

import android.util.Log
import com.example.patchtracker.data.Concentration
import com.example.patchtracker.data.DoseRecord
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Client for uploading treatments to Nightscout.
 */
class NightscoutClient(
    private val baseUrl: String,
    private val apiSecret: String
) {
    private val api: NightscoutApi
    
    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(baseUrl))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        api = retrofit.create(NightscoutApi::class.java)
    }
    
    /**
     * Upload a dose record to Nightscout.
     * @return true if successful, false otherwise
     */
    suspend fun uploadDose(dose: DoseRecord): Result<String> {
        return try {
            val treatment = createTreatment(dose)

            // SECURITY: Never log the API secret
            Log.d("NightscoutClient", "Uploading dose ${dose.id} to Nightscout")

            // Hash the API secret with SHA1 as required by Nightscout
            val hashedSecret = hashApiSecret(apiSecret)

            val response = api.postTreatment(hashedSecret, treatment)

            if (response.isSuccessful) {
                val responseBody = response.body()
                val treatmentId = responseBody?.firstOrNull()?.id ?: "success"
                Log.d("NightscoutClient", "Upload successful: $treatmentId")
                Result.success(treatmentId)
            } else {
                val errorMsg = "Upload failed: ${response.code()} ${response.message()}"
                Log.e("NightscoutClient", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("NightscoutClient", "Upload error: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Create a Nightscout treatment from a dose record.
     */
    private fun createTreatment(dose: DoseRecord): NightscoutTreatment {
        val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val createdAt = iso8601Format.format(Date(dose.createdAtMillis))
        
        val notes = buildString {
            append("CequrPatchLogger: ")
            append("${dose.clicks} clicks, ")
            append("${dose.concentration.name}, ")
            append(dose.insulinName)
        }
        
        return NightscoutTreatment(
            insulin = dose.totalUnits,
            createdAt = createdAt,
            notes = notes
        )
    }
    
    /**
     * Normalize base URL to ensure it ends with a slash.
     */
    private fun normalizeBaseUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    /**
     * Hash the API secret using SHA1 as required by Nightscout.
     */
    private fun hashApiSecret(secret: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(secret.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        /**
         * Validate Nightscout URL.
         * Must be https, or http for localhost/127.0.0.1.
         */
        fun validateUrl(url: String): Boolean {
            if (url.isBlank()) return false

            return when {
                url.startsWith("https://") -> true
                url.startsWith("http://") -> {
                    // Allow http only for localhost
                    url.contains("localhost") || url.contains("127.0.0.1") || url.contains("10.0.2.2")
                }
                else -> false
            }
        }
    }
}

