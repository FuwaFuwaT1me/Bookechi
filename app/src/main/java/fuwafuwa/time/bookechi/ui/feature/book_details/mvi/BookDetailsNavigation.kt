package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_details.ui.BookDetailsScreen
import fuwafuwa.time.bookechi.ui.feature.book_details.ui.BookDetailsScreenV2
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
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
                BookDetailsScreenV2(viewModel)
            } else {
                BookDetailsScreen(viewModel)
            }
        }
    }
}

data class NavigateToBookDetails(
    val bookId: Long
) : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = BookDetailsScreen(bookId)
}
