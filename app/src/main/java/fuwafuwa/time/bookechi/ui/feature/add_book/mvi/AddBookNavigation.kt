package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.ui.AddBookScreen
import fuwafuwa.time.bookechi.ui.feature.add_book.ui.AddBookScreenV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.addBookNavRoot(
    navController: NavController
) {
    composable<AddBookScreen> {
        val viewModel: AddBookViewModel = koinViewModel()
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
                AddBookScreenV2(viewModel)
            } else {
                AddBookScreen(viewModel)
            }
        }
    }
}

data class NavigateToAddBook(
    override val screen: Screen = AddBookScreen,
) : BaseNavigationEvent.NavigateTo
