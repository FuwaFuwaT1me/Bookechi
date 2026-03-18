package fuwafuwa.time.bookechi.ui.feature.book_details.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.model.Book
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.navigation.BookNavType
import fuwafuwa.time.bookechi.ui.feature.book_details.ui.BookDetailsScreen
import fuwafuwa.time.bookechi.ui.feature.book_details.ui.BookDetailsScreenV2
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

@Serializable
data class BookDetailsScreen(
    val book: Book
) : Screen

@OptIn(ExperimentalStdlibApi::class)
fun NavGraphBuilder.bookDetailsNavRoot(
    navController: NavController
) {
    composable<BookDetailsScreen>(
        typeMap = mapOf(
            typeOf<Book>() to BookNavType
        )
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<BookDetailsScreen>()
        val viewModel: BookDetailsViewModel = koinViewModel { parametersOf(route.book) }
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
    val book: Book
) : BaseNavigationEvent.NavigateTo {
    override val screen: Screen = BookDetailsScreen(book)
}

data object NavigateBackToBookDetails : BaseNavigationEvent.NavigateBackTo {
    override val screen: Screen = BookListScreen
}
