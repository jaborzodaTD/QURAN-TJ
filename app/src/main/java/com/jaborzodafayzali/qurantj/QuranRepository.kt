package com.jaborzodafayzali.qurantj

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(private val context:Context){
    private val prefs=context.getSharedPreferences("quran_cache_v5",Context.MODE_PRIVATE)

    fun getSurah(n:Int,language:QuranLanguage):List<Ayah>{
        val cacheKey="sura_${language.code}_$n"
        prefs.getString(cacheKey,null)?.let{return parseCached(it)}
        val arabic=parseArabic(getText(URL("https://api.alquran.cloud/v1/surah/$n/quran-uthmani")))
        if(language.code=="ar"||language.translationKey==null){
            val result=arabic.map{Ayah(it.first,it.second,"")}
            cache(cacheKey,result)
            return result
        }
        val translations=parseTranslations(getText(URL("https://quranenc.com/api/v1/translation/sura/${language.translationKey}/$n")))
        if(translations.isEmpty()||arabic.isEmpty())throw IllegalStateException("Empty Quran response")
        val byAya=translations.associateBy{it.first}
        val result=arabic.mapNotNull{a->byAya[a.first]?.let{Ayah(a.first,a.second,it.second)}}
        if(result.size!=arabic.size)throw IllegalStateException("Arabic/translation ayah mismatch")
        cache(cacheKey,result)
        return result
    }

    private fun cache(key:String,list:List<Ayah>){
        val packed=JSONArray().apply{list.forEach{a->put(JSONObject().apply{put("aya",a.number);put("arabic",a.arabic);put("translation",a.translation)})}}.toString()
        prefs.edit().putString(key,packed).apply()
    }
    private fun parseCached(text:String):List<Ayah>{val a=JSONArray(text);return buildList{for(i in 0 until a.length()){val o=a.getJSONObject(i);add(Ayah(o.getInt("aya"),o.getString("arabic"),o.optString("translation")))}}}
    private fun parseTranslations(text:String):List<Pair<Int,String>>{
        val root=text.trim()
        val arr=if(root.startsWith("["))JSONArray(root) else JSONObject(root).optJSONArray("result")?:JSONObject(root).optJSONArray("data")?:JSONArray()
        return buildList{for(i in 0 until arr.length()){val o=arr.getJSONObject(i);val n=o.optInt("aya",i+1);val v=o.optString("translation").trim();if(v.isNotEmpty())add(n to v)}}
    }
    private fun parseArabic(text:String):List<Pair<Int,String>>{
        val arr=JSONObject(text).getJSONObject("data").getJSONArray("ayahs")
        return buildList{for(i in 0 until arr.length()){val o=arr.getJSONObject(i);add(o.getInt("numberInSurah") to o.getString("text").trim())}}
    }
    private fun getText(url:URL):String{
        val c=url.openConnection() as HttpURLConnection;c.connectTimeout=15000;c.readTimeout=20000;c.requestMethod="GET";c.setRequestProperty("Accept","application/json")
        try{if(c.responseCode !in 200..299)throw IllegalStateException("HTTP "+c.responseCode);return c.inputStream.bufferedReader().use{it.readText()}}finally{c.disconnect()}
    }
}