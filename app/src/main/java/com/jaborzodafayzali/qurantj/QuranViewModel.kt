package com.jaborzodafayzali.qurantj

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = QuranRepository(app)
    var selectedSurah: Surah? by mutableStateOf(null)
    var ayahs: List<Ayah> by mutableStateOf(emptyList())
    var loading by mutableStateOf(false)
    var error: String? by mutableStateOf(null)
    var dark by mutableStateOf(false)
    var fontScale by mutableFloatStateOf(1f)
    var language by mutableStateOf(QuranLanguages.default(app))
        private set

    fun setLanguage(value: QuranLanguage) {
        language = value
        QuranLanguages.save(getApplication(), value)
        selectedSurah?.let(::open)
    }

    fun open(s: Surah) {
        selectedSurah = s; loading = true; error = null
        if (language.translationKey == null) {
            ayahs = emptyList(); loading = false
            error = "Барои ин забон манбаи тасдиқшудаи тарҷума ҳанӯз пайваст нашудааст."
            return
        }
        viewModelScope.launch {
            try {
                ayahs = withContext(Dispatchers.IO) { repo.getSurah(s.number, language) }
                if (ayahs.isEmpty()) error = "Тарҷума барои ин забон дастрас нест."
            } catch (_: Exception) { error = "Матни сура бор нашуд. Интернетро санҷед." }
            finally { loading = false }
        }
    }

    fun back() { selectedSurah = null; ayahs = emptyList(); error = null }
}