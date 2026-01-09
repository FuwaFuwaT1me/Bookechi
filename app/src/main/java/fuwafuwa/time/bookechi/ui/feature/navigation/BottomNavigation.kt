package fuwafuwa.time.bookechi.ui.feature.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.reading_goals.ui.ReadingGoalsScreenRoute
import fuwafuwa.time.bookechi.ui.feature.reading_stats.ui.ReadingStatsScreenRoute
import fuwafuwa.time.bookechi.ui.feature.settings.ui.SettingsScreenRoute
import fuwafuwa.time.bookechi.ui.theme.BlueMain

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Screen,
)

val BottomNavItems = listOf(
    BottomNavItem(
        label = "Bookshelf",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        route = BookListScreen
    ),
    BottomNavItem(
        label = "Stats",
        icon = Icons.Filled.BarChart,
        route = ReadingStatsScreenRoute
    ),
    BottomNavItem(
        label = "Goals",
        icon = Icons.Filled.EmojiEvents,
        route = ReadingGoalsScreenRoute
    ),
    BottomNavItem(
        label = "Settings",
        icon = Icons.Filled.Settings,
        route = SettingsScreenRoute
    ),
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route::class.qualifiedName
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(
                                if (isSelected) 32.dp else 24.dp
                            )
                        ,
                        imageVector = navItem.icon,
                        contentDescription = navItem.label,
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        fontSize = if (isSelected) 14.sp else 12.sp
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BlueMain,
                    selectedTextColor = BlueMain,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color.Transparent,
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBottomNavigationBar() {
    BottomNavigationBar(navController = rememberNavController())
}
