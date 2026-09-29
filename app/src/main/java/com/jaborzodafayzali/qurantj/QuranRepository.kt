package com.jaborzodafayzali.qurantj

import android.content.Context
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("quran_cache", Context.MODE_PRIVATE)
    private val translationKey = "tajik_arifi"

    fun getSurah(n:Int): List<Ayah> {
        val cached = prefs.getString("sura_$n", null)
        if (cached != null) return parse(cached)
        val url = URL("https://quranenc.com/api/v1/translation/sura/$translationKey/$n")
        val c = url.openConnection() as HttpURLConnection
        c.connectTimeout = 15000
        c.readTimeout = 20000
        c.requestMethod = "GET"
        c.setRequestProperty("Accept","application/json")
        val text = c.inputStream.bufferedReader().use { it.readText() }
        c.disconnect()
        prefs.edit().putString("sura_$n", text).apply()
        return parse(text)
    }

    private fun parse(text:String): List<Ayah> {
        val root = org.json.JSONObject(text)
        val arr = when {
            root.has("result") && root.get("result") is JSONArray -> root.getJSONArray("result")
            root.has("result") && root.get("result") is org.json.JSONObject -> JSONArray().put(root.getJSONObject("result"))
            else -> JSONArray()
        }
        return buildList {
            for (i in 0 until arr.length()) {
                val o=arr.getJSONObject(i)
                add(Ayah(
                    o.optInt("aya", i+1),
                    o.optString("arabic_text"),
                    o.optString("translation")
                ))
            }
        }
    }
}
