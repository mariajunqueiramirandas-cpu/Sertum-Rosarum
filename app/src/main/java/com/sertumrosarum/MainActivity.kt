package com.sertumrosarum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onStop() {
        super.onStop()
        lifecycleScope.launch { runCatching { (application as SertumApplication).garden.flush() } }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SertumApp(repository = (application as SertumApplication).garden) }
    }
}
