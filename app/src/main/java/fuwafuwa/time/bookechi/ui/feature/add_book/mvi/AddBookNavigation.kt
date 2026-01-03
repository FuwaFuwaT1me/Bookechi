package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.DataBundle
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.ui.AddBookScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.addBookNavRoot(
    navController: NavController
) {
    composable<AddBookScreen> {
        val viewModel: AddBookViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            AddBookScreen(viewModel)
        }
    }
}

data class NavigateToAddBook(
    override val screen: Screen = AddBookScreen,
    override val dataBundle: DataBundle = object : DataBundle {}
) : BaseNavigationEvent.NavigateTo
