package com.jaborzodafayzali.qurantj

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranViewModel(app:Application): AndroidViewModel(app) {
    private val repo=QuranRepository(app)
    var selectedSurah by androidx.compose.runtime.mutableStateOf<Surah?>(null)
    var ayahs by androidx.compose.runtime.mutableStateOf<List<Ayah>>(emptyList())
    var loading by androidx.compose.runtime.mutableStateOf(false)
    var error by androidx.compose.runtime.mutableStateOf<String?>(null)
    var dark by androidx.compose.runtime.mutableStateOf(false)
    var fontScale by androidx.compose.runtime.mutableFloatStateOf(1f)

    fun open(s:Surah) {
        selectedSurah=s; loading=true; error=null; ayahs=emptyList()
        viewModelScope.launch {
            try { ayahs=withContext(Dispatchers.IO){repo.getSurah(s.number)} }
            catch(e:Exception){ error="Пайвастшавӣ ба манбаи Қуръон муяссар нашуд. Интернетро санҷед." }
            finally { loading=false }
        }
    }
    fun back(){selectedSurah=null; ayahs=emptyList(); error=null}
}
