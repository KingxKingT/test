package com.example.grammardaily.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection
import java.net.URL

/**
 * Optional fast cloud AI. Any free OpenAI-compatible provider works:
 * the user pastes a free API key (Nvidia build.nvidia.com, Groq, OpenRouter...)
 * and long AI answers come back in about a second instead of half a minute.
 * Everything works without it, exactly as before.
 */
object CloudAi {

    data class Provider(val label: String, val baseUrl: String, val model: String, val note: String)

    val providers = listOf(
        Provider(
            "Nvidia",
            "https://integrate.api.nvidia.com/v1",
            "meta/llama-3.1-8b-instruct",
            "Free key: build.nvidia.com \u2014 sign up, click any model, Get API Key"
        ),
        Provider(
            "Groq",
            "https://api.groq.com/openai/v1",
            "llama-3.1-8b-instant",
            "Free key: console.groq.com \u2014 sign up, API Keys, Create API Key"
        ),
        Provider(
            "OpenRouter",
            "https://openrouter.ai/api/v1",
            "meta-llama/llama-3.1-8b-instruct:free",
            "Free key: openrouter.ai \u2014 sign up, Keys, Create Key"
        )
    )

    private const val PREFS = "cloud_ai_state"
    private const val KEY_PROVIDER = "provider_index"
    private const val KEY_API = "api_key"
    private const val KEY_ENABLED = "enabled"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun providerIndex(context: Context): Int =
        prefs(context).getInt(KEY_PROVIDER, 0).coerceIn(0, providers.size - 1)

    fun apiKey(context: Context): String =
        prefs(context).getString(KEY_API, "").orEmpty().trim()

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setProvider(context: Context, index: Int) {
        prefs(context).edit().putInt(KEY_PROVIDER, index).apply()
    }

    fun setApiKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_API, key.trim()).apply()
    }

    fun setEnabled(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, value).apply()
    }

    fun configured(context: Context): Boolean =
        isEnabled(context) && apiKey(context).isNotBlank()

    fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    /** Cloud is truly usable only with a saved key, the switch on, and internet up. */
    fun usableNow(context: Context): Boolean = configured(context) && isOnline(context)

    fun currentProvider(context: Context): Provider =
        providers.getOrNull(providerIndex(context)) ?: providers.first()

    /**
     * One blocking chat call. Returns the reply text, or null on any failure
     * (no key, offline, bad key, provider down) \u2014 callers fall back to the
     * on-phone AI or the offline checker.
     */
    fun chatBlocking(
        context: Context,
        prompt: String,
        maxTokens: Int = 400,
        timeoutMs: Int = 30_000
    ): String? {
        if (!configured(context)) return null
        val provider = currentProvider(context)
        return try {
            val conn = URL(provider.baseUrl.trimEnd('/') + "/chat/completions")
                .openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 8_000
            conn.readTimeout = timeoutMs
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer " + apiKey(context))
            if (provider.baseUrl.contains("openrouter")) {
                conn.setRequestProperty("X-Title", "Grammar Daily")
            }
            val body = "{\"model\":\"" + jsonEscape(provider.model) +
                "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + jsonEscape(prompt) +
                "\"}],\"temperature\":0,\"max_tokens\":" + maxTokens + "}"
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
            conn.disconnect()
            if (code in 200..299) extractContent(text) else null
        } catch (e: Exception) {
            null
        }
    }

    /** Quick test used by the "Test" button in the Laya card. */
    fun testBlocking(context: Context): String {
        if (!configured(context)) return "No key saved yet \u2014 paste one first."
        val reply = chatBlocking(context, "Reply with the single word OK.", maxTokens = 10, timeoutMs = 20_000)
        return when {
            reply != null && reply.trim().startsWith("OK", ignoreCase = true) ->
                "\u2705 The cloud AI answered \u2014 Laya and the AI will now answer much faster."
            reply != null ->
                "The cloud answered something unexpected: " + reply.trim().take(80)
            else ->
                "The cloud did not answer \u2014 check the key, the provider, and the internet. The AI on the phone is still working."
        }
    }

    // --- tiny JSON helpers (no extra libraries) ---

    private fun jsonEscape(s: String): String = buildString {
        for (c in s) when (c) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> if (c < ' ') append("\\u%04x".format(c.code)) else append(c)
        }
    }

    private fun extractContent(body: String): String? {
        val key = "\"content\""
        var idx = body.indexOf(key)
        while (idx >= 0) {
            val colon = body.indexOf(':', idx + key.length)
            if (colon < 0) return null
            var j = colon + 1
            while (j < body.length && body[j].isWhitespace()) j++
            if (j < body.length && body[j] == '"') {
                val value = readJsonString(body, j + 1)
                if (!value.isNullOrBlank()) return value
            }
            idx = body.indexOf(key, idx + key.length)
        }
        return null
    }

    private fun readJsonString(s: String, start: Int): String? {
        val sb = StringBuilder()
        var i = start
        while (i < s.length) {
            val c = s[i]
            when {
                c == '"' -> return sb.toString()
                c == '\\' && i + 1 < s.length -> {
                    val n = s[i + 1]
                    when (n) {
                        'n' -> { sb.append('\n'); i += 2 }
                        't' -> { sb.append('\t'); i += 2 }
                        'r' -> { sb.append('\r'); i += 2 }
                        '"' -> { sb.append('"'); i += 2 }
                        '\\' -> { sb.append('\\'); i += 2 }
                        '/' -> { sb.append('/'); i += 2 }
                        'u' -> {
                            if (i + 5 < s.length) {
                                val code = s.substring(i + 2, i + 6).toIntOrNull(16)
                                if (code != null) sb.append(code.toChar())
                            }
                            i += 6
                        }
                        else -> { sb.append(n); i += 2 }
                    }
                }
                else -> { sb.append(c); i += 1 }
            }
        }
        return null
    }
}
