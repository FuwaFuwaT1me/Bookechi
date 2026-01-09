package fuwafuwa.time.bookechi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.navigation.NavigationHost
import fuwafuwa.time.bookechi.ui.feature.navigation.BottomNavigationBar
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BookechiTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    },
                    content = { innerPadding ->
                        NavigationHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController
                        )
                    }
                )
            }
        }
    }
}
