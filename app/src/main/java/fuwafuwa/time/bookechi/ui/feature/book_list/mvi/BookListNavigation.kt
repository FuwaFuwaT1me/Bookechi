package fuwafuwa.time.bookechi.ui.feature.book_list.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreenV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.bookListNavRoot(
    navController: NavController
) {
    composable<BookListScreen> {
        val viewModel: BookListViewModel = koinViewModel()
        val appPreferences: AppPreferences = koinInject()
        val designPrefs by appPreferences.designPreferences.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            if (designPrefs.useModernDesign) {
                BookListScreenV2(viewModel, appPreferences)
            } else {
                BookListScreen(viewModel)
            }
        }
    }
}
