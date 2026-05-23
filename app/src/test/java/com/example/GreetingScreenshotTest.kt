package com.example

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.LeadRepository
import com.example.ui.B2BLeadCaptureScreen
import com.example.ui.LeadViewModel
import com.example.ui.theme.AuroraGreen
import com.example.ui.theme.DeepBlueBG
import com.example.ui.theme.IceBlueText
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(DeepBlueBG)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "GLOBAL MACHLEADS CRAWLER - ACTIVE",
            color = AuroraGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun b2b_lead_capture_screen_screenshot() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val database = AppDatabase.getDatabase(application)
    val repository = LeadRepository(database.leadDao())
    val viewModel = LeadViewModel(application, repository)

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        B2BLeadCaptureScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/b2b_lead_capture_screen.png")
  }
}
