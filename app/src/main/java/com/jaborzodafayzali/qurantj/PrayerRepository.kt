package com.jaborzodafayzali.qurantj
import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
class PrayerRepository(private val context:Context){
 suspend fun times(p:PrayerLocation,method:Int,school:Int)=withContext(Dispatchers.IO){
  val date=LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
  val u=URL("https://api.aladhan.com/v1/timings/"+date+"?latitude="+p.latitude+"&longitude="+p.longitude+"&method="+method+"&school="+school+"&iso8601=false")
  val t=get(u).getJSONObject("data").getJSONObject("timings")
  PrayerTimes(clean(t.getString("Fajr")),clean(t.getString("Sunrise")),clean(t.getString("Dhuhr")),clean(t.getString("Asr")),clean(t.getString("Maghrib")),clean(t.getString("Isha")),method,school)
 }
 suspend fun qibla(lat:Double,lon:Double)=withContext(Dispatchers.IO){get(URL("https://api.aladhan.com/v1/qibla/"+lat+"/"+lon)).getJSONObject("data").getDouble("direction")}
 suspend fun place(lat:Double,lon:Double)=withContext(Dispatchers.IO){
  val g=Geocoder(context,Locale.getDefault()); val a:Address?=try{g.getFromLocation(lat,lon,1)?.firstOrNull()}catch(_:Exception){null}
  PrayerLocation(a?.locality?:a?.subAdminArea?:"Ҷойгиршавии шумо",a?.countryName?:"",lat,lon)
 }
 private fun clean(v:String)=v.take(5)
 private fun get(u:URL):JSONObject{val c=u.openConnection() as HttpURLConnection;c.connectTimeout=15000;c.readTimeout=20000;c.requestMethod="GET";try{if(c.responseCode !in 200..299)throw IllegalStateException("HTTP "+c.responseCode);return JSONObject(c.inputStream.bufferedReader().use{it.readText()})}finally{c.disconnect()}}
}