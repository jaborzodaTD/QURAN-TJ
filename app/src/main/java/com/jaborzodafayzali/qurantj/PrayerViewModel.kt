package com.jaborzodafayzali.qurantj
import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
class PrayerViewModel(app:Application):AndroidViewModel(app){
 private val repo=PrayerRepository(app)
 var location:PrayerLocation? by mutableStateOf(null)
 var times:PrayerTimes? by mutableStateOf(null)
 var qibla by mutableStateOf(QiblaState(0.0))
 var loading by mutableStateOf(false)
 var error:String? by mutableStateOf(null)
 var method by mutableIntStateOf(14)
 var school by mutableIntStateOf(1)
 fun load(lat:Double,lon:Double){loading=true;viewModelScope.launch{try{val p=withContext(Dispatchers.IO){repo.place(lat,lon)};location=p;reload(p)}catch(_:Exception){error="Маълумоти намоз бор нашуд."}finally{loading=false}}}
 fun useCity(p:PrayerLocation){location=p;reload(p)}
 private fun reload(p:PrayerLocation){loading=true;viewModelScope.launch{try{times=withContext(Dispatchers.IO){repo.times(p,method,school)};qibla=QiblaState(withContext(Dispatchers.IO){repo.qibla(p.latitude,p.longitude)});error=null}catch(_:Exception){error="Маълумоти вақтҳо бор нашуд."}finally{loading=false}}}
}