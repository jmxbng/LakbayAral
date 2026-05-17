package com.example.lakbayaral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lakbayaral.ui.navigation.LakbayAralNavHost
import com.example.lakbayaral.ui.theme.LakbayAralTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LakbayAralTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LakbayAralNavHost()
                }
            }
        }
    }
}
