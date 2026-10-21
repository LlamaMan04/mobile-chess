package com.example.mobilechess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobilechess.ui.theme.MobileChessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow the app content to draw behind the system bars for a modern edge-to-edge layout.
        enableEdgeToEdge()

        // Compose starts at the theme, which then renders the root chess screen.
        setContent {
            MobileChessTheme {
                ChessApp()
            }
        }
    }
}
