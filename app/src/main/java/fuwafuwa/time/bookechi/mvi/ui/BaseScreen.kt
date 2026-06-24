package fuwafuwa.time.bookechi.mvi.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import fuwafuwa.time.bookechi.mvi.api.Action
import fuwafuwa.time.bookechi.mvi.api.State
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BaseScreen(
    navController: NavController,
    viewModel: BaseViewModel<out Action, out State>,
    content: @Composable (NavController) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.model.navigationEvent.collect { navEvent ->
            when (navEvent) {
                is BaseNavigationEvent.NavigateTo -> {
                    navController.navigate(navEvent.screen)
                }
                is BaseNavigationEvent.NavigateBackTo -> {
                    navController.popBackStack(navEvent.screen, false)
                }
                is BaseNavigationEvent.NavigateToStack -> {
                    // 1) Стираем промежуточные экраны (стек завершённой книги) до
                    //    стартового, БЕЗ сохранения — иначе он восстановится при
                    //    переходе на стартовый таб.
                    navController.popBackStack(
                        navController.graph.startDestinationId,
                        inclusive = false,
                        saveState = false,
                    )
                    // 2) Первый экран открываем ровно как переключение таба в нижней
                    //    навигации (теми же опциями), чтобы её save/restore остался
                    //    согласованным и табы продолжали работать. Остальные — поверх.
                    navEvent.screens.forEachIndexed { index, screen ->
                        navController.navigate(screen) {
                            if (index == 0) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
                is BaseNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        content(navController)
    }
}
