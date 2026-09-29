package com.jaborzodafayzali.qurantj

data class PrayerLocation(val city:String,val country:String,val latitude:Double,val longitude:Double)
data class PrayerTimes(val fajr:String,val sunrise:String,val dhuhr:String,val asr:String,val maghrib:String,val isha:String,val method:Int,val school:Int)
data class QiblaState(val bearing:Double,val error:String?=null)
