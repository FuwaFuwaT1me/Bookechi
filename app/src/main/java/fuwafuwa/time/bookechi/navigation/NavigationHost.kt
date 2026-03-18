package fuwafuwa.time.bookechi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fuwafuwa.time.bookechi.mvi.api.NavigationEventFlow
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.addBookNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.bookDetailsNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.bookListNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.library.mvi.libraryNavRoot
import fuwafuwa.time.bookechi.ui.feature.productivity.mvi.productivityNavRoot
import fuwafuwa.time.bookechi.ui.feature.update_progress.mvi.updateProgressNavRoot
import fuwafuwa.time.bookechi.ui.feature.update_result.mvi.updateResultNavRoot

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
        updateResultScenario(navController)

        productivityScenario(navController)

        libraryScenario(navController)
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

private fun NavGraphBuilder.updateResultScenario(navController: NavController) {
    updateResultNavRoot(navController)
}

private fun NavGraphBuilder.libraryScenario(navController: NavController) {
    libraryNavRoot(navController)
}
