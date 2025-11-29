package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.book_list.BookListScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.bookListNavRoot(
    navController: NavController
) {
    composable<BookListScreen> {
        val viewModel: BookListViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            BookListScreen(viewModel)
        }
    }
}
