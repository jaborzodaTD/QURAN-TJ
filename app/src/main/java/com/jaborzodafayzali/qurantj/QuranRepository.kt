package com.jaborzodafayzali.qurantj

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("quran_cache", Context.MODE_PRIVATE)
    private val translationKey = "tajik_arifi"

    fun getSurah(n: Int): List<Ayah> {
        val cached = prefs.getString("sura_$n", null)
        if (cached != null) return parse(cached)

        val url = URL("https://quranenc.com/api/v1/translation/sura/$translationKey/$n")
        val c = url.openConnection() as HttpURLConnection
        c.connectTimeout = 15000
        c.readTimeout = 20000
        c.requestMethod = "GET"
        c.setRequestProperty("Accept", "application/json")

        val code = c.responseCode
        if (code !in 200..299) {
            c.disconnect()
            throw IllegalStateException("HTTP $code")
        }

        val text = c.inputStream.bufferedReader().use { it.readText() }
        c.disconnect()

        val parsed = parse(text)
        if (parsed.isEmpty()) throw IllegalStateException("Empty Quran response")
        prefs.edit().putString("sura_$n", text).apply()
        return parsed
    }

    private fun parse(text: String): List<Ayah> {
        val trimmed = text.trim()
        val arr = when {
            trimmed.startsWith("[") -> JSONArray(trimmed)
            else -> {
                val root = JSONObject(trimmed)
                when {
                    root.opt("result") is JSONArray -> root.getJSONArray("result")
                    root.opt("result") is JSONObject -> JSONArray().put(root.getJSONObject("result"))
                    else -> JSONArray()
                }
            }
        }

        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val arabic = o.optString("arabic_text").trim()
                val translation = o.optString("translation").trim()
                if (arabic.isNotEmpty() && translation.isNotEmpty()) {
                    add(Ayah(o.optInt("aya", i + 1), arabic, translation))
                }
            }
        }
    }
}
