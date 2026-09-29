package com.jaborzodafayzali.qurantj

data class PrayerLocation(val city:String,val country:String,val latitude:Double,val longitude:Double)

data class PrayerTimes(
    val fajr:String,val sunrise:String,val dhuhr:String,val asr:String,val maghrib:String,val isha:String,
    val method:Int,val school:Int
) {
    fun items() = listOf("Fajr" to fajr,"Sunrise" to sunrise,"Dhuhr" to dhuhr,"Asr" to asr,"Maghrib" to maghrib,"Isha" to isha)
}

data class QiblaState(val bearing:Double,val error:String?=null)
