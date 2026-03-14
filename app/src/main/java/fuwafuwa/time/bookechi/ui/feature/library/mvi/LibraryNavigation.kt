package fuwafuwa.time.bookechi.ui.feature.library.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.library.ui.LibraryScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.libraryNavRoot(
    navController: NavController
) {
    composable<LibraryScreen> {
        val viewModel: LibraryViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            LibraryScreen(viewModel)
        }
    }
}
