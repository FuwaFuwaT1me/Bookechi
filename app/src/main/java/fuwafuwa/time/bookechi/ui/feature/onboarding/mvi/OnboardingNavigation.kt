package fuwafuwa.time.bookechi.ui.feature.onboarding.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.onboarding.ui.OnboardingScreen
import fuwafuwa.time.bookechi.ui.feature.onboarding.ui.OnboardingScreenRoute
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.onboardingNavRoot(
    navController: NavController
) {
    composable<OnboardingScreenRoute> {
        val viewModel: OnboardingViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        OnboardingScreen(
            viewModel = viewModel,
            onOpenLibrary = {
                navController.navigate(BookListScreen) {
                    popUpTo(OnboardingScreenRoute) { inclusive = true }
                }
            },
        )
    }
}
