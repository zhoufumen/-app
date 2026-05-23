package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.LeadRepository
import com.example.ui.B2BLeadCaptureScreen
import com.example.ui.LeadViewModel
import com.example.ui.LeadViewModelFactory
import com.example.ui.theme.DeepBlueBG
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Room Database, DAO and Repository
    val database = AppDatabase.getDatabase(this)
    val repository = LeadRepository(database.leadDao())

    // Instantiate LeadViewModel using standard Provider Factory (No heavy DI code)
    val viewModelFactory = LeadViewModelFactory(application, repository)
    val viewModel = ViewModelProvider(this, viewModelFactory)[LeadViewModel::class.java]

    setContent {
      MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DeepBlueBG
        ) {
          B2BLeadCaptureScreen(viewModel = viewModel)
        }
      }
    }
  }
}
