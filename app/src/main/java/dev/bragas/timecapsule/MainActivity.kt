package dev.bragas.timecapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.bragas.timecapsule.ui.theme.TimeCapsuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeCapsuleTheme {
                MyAppNavigation()
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SignInScreenPreview() {
//    TimeCapsuleTheme {
//        SignInScreen()
//    }
//}