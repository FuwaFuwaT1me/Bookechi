package fuwafuwa.time.bookechi.ui.feature.productivity.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.productivityNavRoot(
    navController: NavController
) {
    composable<ProductivityScreen> {
        val viewModel: ProductivityViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            ProductivityScreen(viewModel)
        }
    }
}
