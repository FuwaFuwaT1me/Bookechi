package fuwafuwa.time.bookechi.ui.feature.add_book.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.add_book.ui.AddBookScreen
import fuwafuwa.time.bookechi.ui.feature.add_book.ui.AddBookScreenV2
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.addBookNavRoot(
    navController: NavController
) {
    dialog<AddBookScreen>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val viewModel: AddBookViewModel = koinViewModel()
        val appPreferences: AppPreferences = koinInject()
        val designPrefs by appPreferences.designPreferences.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        LaunchedEffect(Unit) {
            viewModel.model.navigationEvent.collect { navEvent ->
                when (navEvent) {
                    is BaseNavigationEvent.NavigateTo -> {
                        navController.navigate(navEvent.screen)
                    }

                    is BaseNavigationEvent.NavigateBackTo -> {
                        navController.popBackStack(navEvent.screen, false)
                    }

                    is BaseNavigationEvent.NavigateBack -> {
                        navController.popBackStack()
                    }

                    is BaseNavigationEvent.NavigateToStack -> Unit
                }
            }
        }

        if (designPrefs.useModernDesign) {
            AddBookScreenV2(viewModel)
        } else {
            AddBookScreen(viewModel)
        }
    }
}

object NavigateToAddBook : BaseNavigationEvent.NavigateTo {

    override val screen: Screen = AddBookScreen
}
