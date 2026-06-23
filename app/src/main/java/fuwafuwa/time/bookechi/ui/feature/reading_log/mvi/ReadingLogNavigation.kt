package fuwafuwa.time.bookechi.ui.feature.reading_log.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.reading_log.ui.ReadingLogScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.readingLogNavRoot(
    navController: NavController
) {
    composable<ReadingLogScreen> { backStackEntry ->
        val route = backStackEntry.toRoute<ReadingLogScreen>()
        val viewModel: ReadingLogViewModel = koinViewModel { parametersOf(route.bookId) }

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            ReadingLogScreen(viewModel = viewModel)
        }
    }
}

/** bookId = -1 — полный журнал (все книги); иначе журнал конкретной книги. */
data class NavigateToReadingLog(
    val bookId: Long = -1L,
) : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = ReadingLogScreen(bookId = bookId)
}
