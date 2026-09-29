package com.jaborzodafayzali.qurantj
import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
class QuranViewModel(app:Application):AndroidViewModel(app){
 private val repo=QuranRepository(app)
 var selectedSurah:Surah? by mutableStateOf(null)
 var ayahs:List<Ayah> by mutableStateOf(emptyList())
 var loading by mutableStateOf(false)
 var error:String? by mutableStateOf(null)
 var dark by mutableStateOf(false)
 var fontScale by mutableFloatStateOf(1f)
 fun open(s:Surah){selectedSurah=s;loading=true;error=null;viewModelScope.launch{try{ayahs=withContext(Dispatchers.IO){repo.getSurah(s.number)}}catch(_:Exception){error="Матни сура бор нашуд. Интернетро санҷед."}finally{loading=false}}}
 fun back(){selectedSurah=null;ayahs=emptyList();error=null}
}