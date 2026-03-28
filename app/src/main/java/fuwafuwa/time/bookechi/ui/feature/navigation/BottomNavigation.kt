package fuwafuwa.time.bookechi.ui.feature.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.R
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.library.ui.LibraryScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.theme.BottomBarDivider
import fuwafuwa.time.bookechi.ui.theme.FigmaBottomNavSelectedTab
import fuwafuwa.time.bookechi.ui.theme.FigmaSubtitle

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
        route = LibraryScreen
    ),
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .border(
                width = 1.dp,
                color = BottomBarDivider,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
        ,
        containerColor = Color.White,
        tonalElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            BottomNavItems.forEach { navItem ->
                val isSelected = currentRoute == navItem.route::class.qualifiedName

                NavigationItem(
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
                    navItem = navItem,
                    selectedColor = FigmaBottomNavSelectedTab,
                    unselectedColor = FigmaSubtitle
                )
            }
        }
    }
}

@Composable
private fun NavigationItem(
    selected: Boolean,
    navItem: BottomNavItem,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    val color = if (selected) { selectedColor } else unselectedColor

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                onClick()
            }
        ,
    ) {
        Icon(
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.CenterHorizontally)
            ,
            painter = painterResource(navItem.resId),
            tint = color,
            contentDescription = navItem.label,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp)
            ,
            text = navItem.label,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun PreviewBottomNavigationBar() {
    BottomNavigationBar(navController = rememberNavController())
}
