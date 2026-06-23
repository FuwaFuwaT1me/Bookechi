package fuwafuwa.time.bookechi.ui.feature.read_shelf.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.ui.ReadShelfScreen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.ui.ShelfBookScreen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.ui.ShelfScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.readShelfNavRoot(
    navController: NavController
) {
    composable<ReadShelfScreen> {
        val viewModel: ShelfViewModel = koinViewModel()
        LaunchedEffect(Unit) { viewModel.init() }
        BaseScreen(navController = navController, viewModel = viewModel) {
            ShelfScreen(viewModel = viewModel)
        }
    }

    composable<ShelfBookScreen> { backStackEntry ->
        val route = backStackEntry.toRoute<ShelfBookScreen>()
        val viewModel: ShelfBookViewModel = koinViewModel { parametersOf(route.bookId) }
        LaunchedEffect(Unit) { viewModel.init() }
        BaseScreen(navController = navController, viewModel = viewModel) {
            ShelfBookScreen(viewModel = viewModel)
        }
    }
}

object NavigateToReadShelf : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = ReadShelfScreen
}

data class NavigateToShelfBook(val bookId: Long) : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = ShelfBookScreen(bookId = bookId)
}
