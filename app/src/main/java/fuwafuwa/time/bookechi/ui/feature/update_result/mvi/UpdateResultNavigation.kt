package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_result.ui.UpdateResultScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.updateResultNavRoot(
    navController: NavController
) {
    composable<UpdateResultScreen> { backStackEntry ->
        val route = backStackEntry.toRoute<UpdateResultScreen>()
        val viewModel: UpdateResultViewModel = koinViewModel {
            parametersOf(
                route.startPages,
                route.updatedPages,
                route.bookAllPages
            )
        }

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            UpdateResultScreen(viewModel = viewModel)
        }
    }
}

data class NavigateToUpdateResult(
    val startPages: Int,
    val updatedPages: Int,
    val allBookPages: Int,
) : BaseNavigationEvent.NavigateTo {

    override val screen: Screen = UpdateResultScreen(
        startPages, updatedPages, allBookPages
    )
}
