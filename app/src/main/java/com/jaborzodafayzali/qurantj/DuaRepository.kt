package com.jaborzodafayzali.qurantj

data class Dua(val ref:String,val arabic:String,val translation:String)

class DuaRepository{
    fun load(language:QuranLanguage,refs:List<String>):List<Dua>{
        val bySurah=refs.groupBy{it.substringBefore(":").toInt()}
        return buildList{
            for((sura,items) in bySurah){
                val ar=read("https://api.alquran.cloud/v1/surah/$sura/quran-uthmani")
                val aa=org.json.JSONObject(ar).getJSONObject("data").getJSONArray("ayahs")
                val wanted=items.map{it.substringAfter(":").toInt()}.toSet()
                val translations=if(language.translationKey==null) emptyMap() else {
                    val tr=read("https://quranenc.com/api/v1/translation/sura/${language.translationKey}/$sura")
                    val ta=org.json.JSONArray(tr)
                    buildMap{for(i in 0 until ta.length()){val o=ta.getJSONObject(i);put(o.optInt("aya",i+1),o.optString("translation"))}}
                }
                for(i in 0 until aa.length()){
                    val o=aa.getJSONObject(i);val n=o.getInt("numberInSurah")
                    if(n in wanted)add(Dua("$sura:$n",o.getString("text"),translations[n]?:""))
                }
            }
        }
    }
    private fun read(url:String):String{
        val c=(java.net.URL(url).openConnection() as java.net.HttpURLConnection)
        c.connectTimeout=15000;c.readTimeout=20000;c.requestMethod="GET"
        try{if(c.responseCode !in 200..299)throw IllegalStateException("HTTP "+c.responseCode);return c.inputStream.bufferedReader().use{it.readText()}}finally{c.disconnect()}
    }
}