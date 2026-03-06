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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.feature.reading_goals.ui.ReadingGoalsScreenRoute
import fuwafuwa.time.bookechi.ui.feature.reading_stats.ui.ReadingStatsScreenRoute
import fuwafuwa.time.bookechi.ui.feature.settings.ui.SettingsScreenRoute
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import fuwafuwa.time.bookechi.ui.theme.FigmaBottomNavSelectedTab
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle
import fuwafuwa.time.bookechi.ui.theme.FigmaTitle

data class BottomNavItem(
    val label: String,
    val resId: Int,
    val route: Screen,
)

val BottomNavItems = listOf(
    BottomNavItem(
        label = "Активность",
        resId = R.drawable.feather,
        route = BookListScreen
    ),
    BottomNavItem(
        label = "Продуктивность",
        resId = R.drawable.bar_chart,
        route = ProductivityScreen
    ),
    BottomNavItem(
        label = "Библиотека",
        resId = R.drawable.book_open,
        route = ProductivityScreen
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
                        painter = painterResource(navItem.resId),
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
                    selectedIconColor = FigmaBottomNavSelectedTab,
                    selectedTextColor = FigmaTitle,
                    unselectedIconColor = FigmaSubtitle,
                    unselectedTextColor = FigmaSubtitle,
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
