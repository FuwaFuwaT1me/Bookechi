package fuwafuwa.time.bookechi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.navigation.NavigationHost
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.library.ui.LibraryScreen
import fuwafuwa.time.bookechi.ui.feature.navigation.BottomNavigationBar
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.DarkCanvas
import fuwafuwa.time.bookechi.ui.theme.LinenCanvas
import fuwafuwa.time.bookechi.ui.theme.LocalBottomBarHeight
import fuwafuwa.time.bookechi.ui.theme.LocalThemeToggle
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val appPreferences = koinInject<AppPreferences>()
            val isDarkTheme by appPreferences.isDarkTheme.collectAsState()

            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(
                color = if (isDarkTheme) DarkCanvas else LinenCanvas,
                darkIcons = !isDarkTheme,
            )

            BookechiTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val tabRoutes = setOf(
                    BookListScreen::class.qualifiedName,
                    ProductivityScreen::class.qualifiedName,
                    LibraryScreen::class.qualifiedName,
                )
                val showBottomBar = currentRoute in tabRoutes

                val density = LocalDensity.current
                var barHeight by remember { mutableStateOf(0.dp) }

                CompositionLocalProvider(
                    LocalThemeToggle provides { appPreferences.toggleDarkTheme() },
                    LocalBottomBarHeight provides if (showBottomBar) barHeight else 0.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BookechiTheme.colors.canvas),
                    ) {
                        // Контент во всю высоту — скроллится ПОД плавающим баром
                        // и виден в его скруглённых углах.
                        NavigationHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                            navController = navController,
                        )

                        if (showBottomBar) {
                            BottomNavigationBar(
                                navController = navController,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .onSizeChanged {
                                        barHeight = with(density) { it.height.toDp() }
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}
