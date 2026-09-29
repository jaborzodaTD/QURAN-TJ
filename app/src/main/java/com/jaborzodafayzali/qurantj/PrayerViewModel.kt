package com.jaborzodafayzali.qurantj
import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
class PrayerViewModel(app:Application):AndroidViewModel(app){
 private val repo=PrayerRepository(app);private val prefs=app.getSharedPreferences("prayer_settings",0)
 var location:PrayerLocation? by mutableStateOf(null);var times:PrayerTimes? by mutableStateOf(null);var qibla by mutableStateOf(QiblaState(0.0));var loading by mutableStateOf(false);var error:String? by mutableStateOf(null)
 var prayerMethod by mutableIntStateOf(prefs.getInt("method",3));var madhhab by mutableIntStateOf(prefs.getInt("school",1))
 fun load(lat:Double,lon:Double){loading=true;viewModelScope.launch{try{val p=withContext(Dispatchers.IO){repo.place(lat,lon)};location=p;reload(p)}catch(_:Exception){error="Не удалось определить местоположение.";loading=false}}}
 fun useCity(p:PrayerLocation){location=p;reload(p)}
 fun selectMethod(v:Int){prayerMethod=v;prefs.edit().putInt("method",v).apply();location?.let(::reload)}
 fun selectMadhhab(v:Int){madhhab=v;prefs.edit().putInt("school",v).apply();location?.let(::reload)}
 private fun reload(p:PrayerLocation){loading=true;viewModelScope.launch{try{times=withContext(Dispatchers.IO){repo.times(p,prayerMethod,madhhab)};qibla=QiblaState(withContext(Dispatchers.IO){repo.qibla(p.latitude,p.longitude)});error=null}catch(_:Exception){error="Не удалось загрузить время намаза."}finally{loading=false}}}
}
