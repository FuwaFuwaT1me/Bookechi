package fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.reading_goals.ui.ReadingGoalsScreen
import fuwafuwa.time.bookechi.ui.feature.reading_goals.ui.ReadingGoalsScreenRoute
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.readingGoalsNavRoot(
    navController: NavController
) {
    composable<ReadingGoalsScreenRoute> {
        val viewModel: ReadingGoalsViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            ReadingGoalsScreen(viewModel)
        }
    }
}
