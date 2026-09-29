package com.jaborzodafayzali.qurantj

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("quran_cache_v4", Context.MODE_PRIVATE)

    fun getSurah(n: Int, language: QuranLanguage): List<Ayah> {
        val key = language.translationKey ?: return emptyList()
        val cacheKey = "sura_" + language.code + "_" + n
        prefs.getString(cacheKey, null)?.let { return parseCached(it) }

        val translationJson = getText(URL("https://quranenc.com/api/v1/translation/sura/" + key + "/" + n))
        val arabicJson = getText(URL("https://api.alquran.cloud/v1/surah/" + n + "/quran-uthmani"))
        val translations = parseTranslations(translationJson)
        val arabic = parseArabic(arabicJson)
        val count = minOf(translations.size, arabic.size)
        if (count == 0) throw IllegalStateException("Empty Quran response")

        val result = buildList {
            for (i in 0 until count) add(Ayah(translations[i].first, arabic[i].second, translations[i].second))
        }
        val packed = JSONArray().apply {
            result.forEach { a ->
                put(JSONObject().apply {
                    put("aya", a.number)
                    put("arabic", a.arabic)
                    put("translation", a.translation)
                })
            }
        }.toString()
        prefs.edit().putString(cacheKey, packed).apply()
        return result
    }

    private fun parseCached(text: String): List<Ayah> {
        val a = JSONArray(text)
        return buildList {
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                add(Ayah(o.getInt("aya"), o.getString("arabic"), o.getString("translation")))
            }
        }
    }

    private fun parseTranslations(text: String): List<Pair<Int, String>> {
        val root = text.trim()
        val arr = if (root.startsWith("[")) JSONArray(root) else JSONObject(root).optJSONArray("result") ?: JSONArray()
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val number = o.optInt("aya", i + 1)
                val value = o.optString("translation").trim()
                if (value.isNotEmpty()) add(number to value)
            }
        }
    }

    private fun parseArabic(text: String): List<Pair<Int, String>> {
        val arr = JSONObject(text).getJSONObject("data").getJSONArray("ayahs")
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(o.getInt("numberInSurah") to o.getString("text").trim())
            }
        }
    }

    private fun getText(url: URL): String {
        val c = url.openConnection() as HttpURLConnection
        c.connectTimeout = 15000
        c.readTimeout = 20000
        c.requestMethod = "GET"
        c.setRequestProperty("Accept", "application/json")
        try {
            if (c.responseCode !in 200..299) throw IllegalStateException("HTTP " + c.responseCode)
            return c.inputStream.bufferedReader().use { it.readText() }
        } finally { c.disconnect() }
    }
}