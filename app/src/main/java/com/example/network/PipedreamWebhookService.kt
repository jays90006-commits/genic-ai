package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class WebhookResult {
  data class Success(
    val statusCode: Int,
    val responseBody: String,
    val payloadPreview: String,
    val timestamp: Long
  ) : WebhookResult()

  data class Error(
    val message: String,
    val statusCode: Int? = null,
    val rawDetails: String? = null
  ) : WebhookResult()

  data class ConfigurationRequired(
    val message: String,
    val currentUrl: String
  ) : WebhookResult()
}

class PipedreamWebhookService {

  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(25, TimeUnit.SECONDS)
    .readTimeout(25, TimeUnit.SECONDS)
    .writeTimeout(25, TimeUnit.SECONDS)
    .build()

  companion object {
    const val DEFAULT_PLACEHOLDER_URL = "https://[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
  }

  suspend fun sendStoryToWebhook(
    url: String,
    story: String
  ): WebhookResult = withContext(Dispatchers.IO) {
    val cleanUrl = url.trim()

    // Validate if URL is empty or unconfigured placeholder
    if (cleanUrl.isEmpty()) {
      return@withContext WebhookResult.Error("Webhook URL cannot be empty. Please provide a valid Pipedream URL.")
    }

    if (cleanUrl.contains("[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]") ||
      cleanUrl == "https://[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]" ||
      cleanUrl == "[APNA_PIPEDREAM_URL_YAHAN_PASTE_KAREIN]"
    ) {
      return@withContext WebhookResult.ConfigurationRequired(
        message = "Placeholder URL detected. Please update with your actual Pipedream webhook endpoint.",
        currentUrl = cleanUrl
      )
    }

    if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
      return@withContext WebhookResult.Error(
        "Invalid URL format. URL must begin with 'https://' or 'http://'"
      )
    }

    try {
      val wordCount = story.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
      val timestamp = System.currentTimeMillis()

      // Build JSON payload
      val jsonPayload = JSONObject().apply {
        put("action", "generate_video")
        put("app", "Genic AI")
        put("story", story)
        put("wordCount", wordCount)
        put("characterCount", story.length)
        put("timestamp", timestamp)
        put("isoDate", java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date(timestamp)))
      }.toString(2)

      val requestBody = jsonPayload.toRequestBody(JSON_MEDIA_TYPE)
      val request = Request.Builder()
        .url(cleanUrl)
        .post(requestBody)
        .addHeader("User-Agent", "GenicAI-Android/1.0")
        .addHeader("Content-Type", "application/json")
        .build()

      client.newCall(request).execute().use { response ->
        val statusCode = response.code
        val responseText = response.body?.string().orEmpty().ifBlank { "No response body returned" }

        if (response.isSuccessful) {
          WebhookResult.Success(
            statusCode = statusCode,
            responseBody = responseText,
            payloadPreview = jsonPayload,
            timestamp = timestamp
          )
        } else {
          WebhookResult.Error(
            message = "Server returned HTTP $statusCode (${response.message})",
            statusCode = statusCode,
            rawDetails = responseText
          )
        }
      }
    } catch (e: IllegalArgumentException) {
      WebhookResult.Error("Malformed Webhook URL: ${e.message}")
    } catch (e: IOException) {
      WebhookResult.Error("Network error: Could not reach webhook endpoint (${e.localizedMessage ?: "Connection failed"})")
    } catch (e: Exception) {
      WebhookResult.Error("Unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
    }
  }
}
