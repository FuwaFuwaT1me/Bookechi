package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_details.ui.BookDetailsScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class BookDetailsScreen(
    val bookId: Long
) : Screen

fun NavGraphBuilder.bookDetailsNavRoot(
    navController: NavController
) {
    composable<BookDetailsScreen> { backStackEntry ->
        val route = backStackEntry.toRoute<BookDetailsScreen>()
        val viewModel: BookDetailsViewModel = koinViewModel { parametersOf(route.bookId) }

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            BookDetailsScreen(viewModel)
        }
    }
}

data class NavigateToBookDetails(
    val bookId: Long
) : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = BookDetailsScreen(bookId)
}
