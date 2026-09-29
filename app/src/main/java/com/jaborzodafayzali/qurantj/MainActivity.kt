package com.jaborzodafayzali.qurantj

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = AndroidColor.rgb(3, 27, 19)
        window.navigationBarColor = AndroidColor.rgb(6, 26, 19)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent { QuranTJApp(viewModel()) }
    }
}
