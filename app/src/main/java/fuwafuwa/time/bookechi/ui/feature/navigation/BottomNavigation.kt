package fuwafuwa.time.bookechi.ui.feature.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.library.ui.LibraryScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Screen,
)

val BottomNavItems = listOf(
    BottomNavItem(
        label = "Активность",
        icon = Icons.Rounded.LocalFireDepartment,
        route = BookListScreen
    ),
    BottomNavItem(
        label = "Продуктивность",
        icon = Icons.AutoMirrored.Rounded.TrendingUp,
        route = ProductivityScreen
    ),
    BottomNavItem(
        label = "Библиотека",
        icon = Icons.Rounded.CollectionsBookmark,
        route = LibraryScreen
    ),
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val colors = BookechiTheme.colors
    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
        ,
        containerColor = colors.surfaceElevated,
        tonalElevation = 0.dp,
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
                    selectedColor = colors.accentDeep,
                    unselectedColor = colors.textSecondary
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
    val color by animateColorAsState(
        targetValue = if (selected) selectedColor else unselectedColor,
        animationSpec = tween(250),
        label = "navItemColor",
    )

    // Лёгкий «pop» по масштабу у активного таба.
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "navItemScale",
    )

    // Полный оборот иконки при выборе таба.
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(selected) {
        if (selected) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = rotation.value
                    scaleX = scale
                    scaleY = scale
                },
            imageVector = navItem.icon,
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
    BookechiTheme {
        BottomNavigationBar(navController = rememberNavController())
    }
}
