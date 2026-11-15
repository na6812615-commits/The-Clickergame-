package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.TapCoreApp
import com.example.ui.theme.TapCoreTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        GameViewModel.Factory(GameRepository(db.gameStateDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            TapCoreTheme {
                TapCoreApp(viewModel = gameViewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        gameViewModel.flushSave()
    }
}
