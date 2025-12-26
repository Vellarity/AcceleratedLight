package com.vellarity.lightaccs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vellarity.lightaccs.ui.screen.MainScreen
import com.vellarity.lightaccs.ui.screen.MainScreenRoot
import com.vellarity.lightaccs.ui.screen.MainScreenViewModel
import com.vellarity.lightaccs.ui.theme.LightAccsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightAccsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreenRoot()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LightAccsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreenRoot()
        }
    }
}