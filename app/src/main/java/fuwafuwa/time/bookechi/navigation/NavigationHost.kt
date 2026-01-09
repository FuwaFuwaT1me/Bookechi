package fuwafuwa.time.bookechi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.addBookNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.bookDetailsNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.bookListNavRoot
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.readingGoalsNavRoot
import fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi.readingStatsNavRoot
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.settingsNavRoot

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BookListScreen
    ) {
        bookListScenario(navController)
        addBookScenario(navController)
        bookDetailsScenario(navController)
        readingStatsScenario(navController)
        readingGoalsScenario(navController)
        settingsScenario(navController)
    }
}

private fun NavGraphBuilder.bookListScenario(navController: NavController) {
    bookListNavRoot(navController)
}

private fun NavGraphBuilder.addBookScenario(navController: NavController) {
    addBookNavRoot(navController)
}

private fun NavGraphBuilder.bookDetailsScenario(navController: NavController) {
    bookDetailsNavRoot(navController)
}

private fun NavGraphBuilder.readingStatsScenario(navController: NavController) {
    readingStatsNavRoot(navController)
}

private fun NavGraphBuilder.readingGoalsScenario(navController: NavController) {
    readingGoalsNavRoot(navController)
}

private fun NavGraphBuilder.settingsScenario(navController: NavController) {
    settingsNavRoot(navController)
}
