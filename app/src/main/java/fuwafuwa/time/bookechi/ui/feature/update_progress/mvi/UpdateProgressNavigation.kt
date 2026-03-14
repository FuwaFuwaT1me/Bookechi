package fuwafuwa.time.bookechi.ui.feature.update_progress.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.update_progress.ui.UpdateProgressScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
fun NavGraphBuilder.updateProgressNavRoot(
    navController: NavController
) {
    composable<UpdateProgressScreen>(
        typeMap = mapOf(
            typeOf<Book>() to NavType.ParcelableType(Book::class.java)
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<UpdateProgressScreen>()
        val viewModel: UpdateProgressViewModel = koinViewModel {
            parametersOf(route.book)
        }

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            UpdateProgressScreen(viewModel = viewModel)
        }
    }
}

data class NavigateToUpdateProgress(
    val book: Book,
) : BaseNavigationEvent.NavigateTo {

    override val screen: Screen = UpdateProgressScreen(book)
}
