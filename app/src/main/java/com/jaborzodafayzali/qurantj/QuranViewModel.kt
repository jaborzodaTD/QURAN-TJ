package com.jaborzodafayzali.qurantj

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = QuranRepository(app)

    var selectedSurah: Surah? by mutableStateOf(null)
    var ayahs: List<Ayah> by mutableStateOf(emptyList())
    var loading: Boolean by mutableStateOf(false)
    var error: String? by mutableStateOf(null)
    var dark: Boolean by mutableStateOf(false)
    var fontScale: Float by mutableFloatStateOf(1f)

    fun open(s: Surah) {
        selectedSurah = s
        loading = true
        error = null
        ayahs = emptyList()
        viewModelScope.launch {
            try {
                ayahs = withContext(Dispatchers.IO) { repo.getSurah(s.number) }
            } catch (e: Exception) {
                error = "Пайвастшавӣ ба манбаи Қуръон муяссар нашуд. Интернетро санҷед."
            } finally {
                loading = false
            }
        }
    }

    fun back() {
        selectedSurah = null
        ayahs = emptyList()
        error = null
    }
}
