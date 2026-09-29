package com.jaborzodafayzali.qurantj

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class QuranRepository(private val context:Context){
    private val prefs=context.getSharedPreferences("quran_cache_v6",Context.MODE_PRIVATE)

    fun getSurah(n:Int,language:QuranLanguage):List<Ayah>{
        require(n in 1..114)
        val key="sura_${language.code}_$n"
        prefs.getString(key,null)?.let{runCatching{parseCached(it)}.getOrNull()?.takeIf{list->list.isNotEmpty()}?.let{return it}}
        val arabic=parseArabic(getText(URL("https://api.alquran.cloud/v1/surah/$n/quran-uthmani")))
        if(arabic.isEmpty()) throw IllegalStateException("Arabic Quran response is empty")
        if(language.code=="ar" || language.translationKey==null){
            return arabic.map{Ayah(it.first,it.second,"")}.also{cache(key,it)}
        }
        val translationKey=findTranslationKey(language)
        val translations=parseTranslations(getText(URL("https://quranenc.com/api/v1/translation/sura/$translationKey/$n")))
        val byAya=translations.associateBy{it.first}
        if(translations.isEmpty()) throw IllegalStateException("Translation source returned no ayahs")
        val result=arabic.map{a->byAya[a.first]?.let{Ayah(a.first,a.second,it.second)} ?: Ayah(a.first,a.second,"")}
        if(result.count{it.translation.isNotBlank()} < (arabic.size*0.95f).toInt())
            throw IllegalStateException("Translation ayah alignment failed")
        cache(key,result)
        return result
    }

    private fun findTranslationKey(language:QuranLanguage):String{
        val list=runCatching{getText(URL("https://quranenc.com/api/v1/translations/list/${language.code}?localization=${language.code}"))}.getOrNull()
        if(list!=null){
            val arr=runCatching{JSONArray(list)}.getOrNull()
            if(arr!=null){
                var fallback:String?=null
                for(i in 0 until arr.length()){
                    val o=arr.optJSONObject(i)?:continue
                    val k=o.optString("key")
                    if(k==language.translationKey)return k
                    if(fallback==null && k.isNotBlank())fallback=k
                }
                if(fallback!=null)return fallback
            }
        }
        return language.translationKey ?: throw IllegalStateException("No translation source")
    }

    private fun cache(key:String,list:List<Ayah>){
        val packed=JSONArray().apply{list.forEach{a->put(JSONObject().apply{
            put("aya",a.number);put("arabic",a.arabic);put("translation",a.translation)
        })}}.toString()
        prefs.edit().putString(key,packed).apply()
    }
    private fun parseCached(text:String):List<Ayah>{
        val a=JSONArray(text);return buildList{for(i in 0 until a.length()){
            val o=a.getJSONObject(i);add(Ayah(o.getInt("aya"),o.getString("arabic"),o.optString("translation")))
        }}
    }
    private fun parseTranslations(text:String):List<Pair<Int,String>>{
        val arr=runCatching{JSONArray(text)}.getOrElse{
            val o=JSONObject(text);o.optJSONArray("result")?:o.optJSONArray("data")?:JSONArray()
        }
        return buildList{for(i in 0 until arr.length()){
            val o=arr.optJSONObject(i)?:continue
            val n=o.optInt("aya",i+1);val v=o.optString("translation").trim()
            if(v.isNotEmpty())add(n to v)
        }}
    }
    private fun parseArabic(text:String):List<Pair<Int,String>>{
        val arr=JSONObject(text).getJSONObject("data").getJSONArray("ayahs")
        return buildList{for(i in 0 until arr.length()){
            val o=arr.getJSONObject(i);add(o.getInt("numberInSurah") to o.getString("text").trim())
        }}
    }
    private fun getText(url:URL):String{
        val c=url.openConnection() as HttpURLConnection
        c.connectTimeout=10000;c.readTimeout=15000;c.useCaches=true
        c.requestMethod="GET";c.setRequestProperty("Accept","application/json")
        try{if(c.responseCode !in 200..299)throw IllegalStateException("HTTP "+c.responseCode)
            return c.inputStream.bufferedReader().use{it.readText()}
        }finally{c.disconnect()}
    }
}