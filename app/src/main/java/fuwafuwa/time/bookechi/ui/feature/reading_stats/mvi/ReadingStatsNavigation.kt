package fuwafuwa.time.bookechi.ui.feature.reading_stats.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.reading_stats.ui.ReadingStatsScreen
import fuwafuwa.time.bookechi.ui.feature.reading_stats.ui.ReadingStatsScreenRoute
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.readingStatsNavRoot(
    navController: NavController
) {
    composable<ReadingStatsScreenRoute> {
        val viewModel: ReadingStatsViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            ReadingStatsScreen(viewModel)
        }
    }
}

