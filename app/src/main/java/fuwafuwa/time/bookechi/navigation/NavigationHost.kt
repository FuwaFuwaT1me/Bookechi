package fuwafuwa.time.bookechi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.addBookNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_details.mvi.bookDetailsNavRoot
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.book_list.mvi.bookListNavRoot

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BookListScreen
    ) {
        bookListScenario(navController)
        addBookScenario(navController)
        bookDetailsScenario(navController)
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
