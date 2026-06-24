package fuwafuwa.time.bookechi.ui.feature.update_result.mvi

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fuwafuwa.time.bookechi.mvi.impl.BaseNavigationEvent
import fuwafuwa.time.bookechi.mvi.ui.BaseScreen
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.book_list.ui.BookListScreen
import fuwafuwa.time.bookechi.ui.feature.productivity.ui.ProductivityScreen
import fuwafuwa.time.bookechi.ui.feature.read_shelf.ui.ReadShelfScreen
import fuwafuwa.time.bookechi.ui.feature.update_result.ui.UpdateResultScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Возврат на главный экран — деталей книги может не быть в стеке (путь Home→прогресс→успех). */
object NavigateBackToHome : BaseNavigationEvent.NavigateBackTo {
    override val screen: Screen = BookListScreen
}

/** «Далее» с экрана «Книга прочитана» → на экран продуктивности (стек сброшен до главной). */
object NavigateToProductivityRoot : BaseNavigationEvent.NavigateToStack {
    override val screens: List<Screen> = listOf(ProductivityScreen)
}

/** «На полку» с экрана «Книга прочитана» → полка, под ней — продуктивность («назад» → продуктивность). */
object NavigateToShelfFromFinish : BaseNavigationEvent.NavigateToStack {
    override val screens: List<Screen> = listOf(ProductivityScreen, ReadShelfScreen)
}

fun NavGraphBuilder.updateResultNavRoot(
    navController: NavController
) {
    composable<UpdateResultScreen> { backStackEntry ->
        val route = backStackEntry.toRoute<UpdateResultScreen>()
        val viewModel: UpdateResultViewModel = koinViewModel {
            parametersOf(
                UpdateResultArgs(
                    startPages = route.startPages,
                    updatedPages = route.updatedPages,
                    bookAllPages = route.bookAllPages,
                    bookId = route.bookId,
                    streakExtended = route.streakExtended,
                    readingTimeMinutes = route.readingTimeMinutes,
                    bookName = route.bookName,
                    bookAuthor = route.bookAuthor,
                    coverPath = route.coverPath,
                )
            )
        }

        LaunchedEffect(Unit) {
            viewModel.init()
        }

        BaseScreen(
            navController = navController,
            viewModel = viewModel
        ) {
            UpdateResultScreen(viewModel = viewModel)
        }
    }
}

data class NavigateToUpdateResult(
    val startPages: Int,
    val updatedPages: Int,
    val allBookPages: Int,
    val bookId: Long = -1L,
    val streakExtended: Boolean = false,
    val readingTimeMinutes: Int = 0,
    val bookName: String = "",
    val bookAuthor: String = "",
    val coverPath: String = "",
) : BaseNavigationEvent.NavigateTo {

    override val screen: Screen = UpdateResultScreen(
        startPages = startPages,
        updatedPages = updatedPages,
        bookAllPages = allBookPages,
        bookId = bookId,
        streakExtended = streakExtended,
        readingTimeMinutes = readingTimeMinutes,
        bookName = bookName,
        bookAuthor = bookAuthor,
        coverPath = coverPath,
    )
}

/**
 * Аргументы создания [UpdateResultViewModel] — одним объектом, чтобы не упираться
 * в лимит деструктуризации параметров Koin.
 */
data class UpdateResultArgs(
    val startPages: Int,
    val updatedPages: Int,
    val bookAllPages: Int,
    val bookId: Long,
    val streakExtended: Boolean,
    val readingTimeMinutes: Int,
    val bookName: String,
    val bookAuthor: String,
    val coverPath: String,
)
