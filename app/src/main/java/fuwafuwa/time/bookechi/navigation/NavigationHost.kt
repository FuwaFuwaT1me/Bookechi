package fuwafuwa.time.bookechi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.addBookNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.bookDetailsNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.bookListNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.productivityNavRoot
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.updateProgressNavRoot

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
        updateProgressScenario(navController)

        productivityScenario(navController)
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

private fun NavGraphBuilder.productivityScenario(navController: NavController) {
    productivityNavRoot(navController)
}

private fun NavGraphBuilder.updateProgressScenario(navController: NavController) {
    updateProgressNavRoot(navController)
}
