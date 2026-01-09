package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.settings.ui.SettingsScreen
import fuwafuwa.time.bookechi.ui.feature.settings.ui.SettingsScreenRoute
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.settingsNavRoot(
    navController: NavController
) {
    composable<SettingsScreenRoute> {
        val viewModel: SettingsViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            SettingsScreen(viewModel)
        }
    }
}

