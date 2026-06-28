package fuwafuwa.time.bookechi

import android.app.Application
import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.sync.SyncManager
import fuwafuwa.time.bookechi.di.authModule
import fuwafuwa.time.bookechi.di.databaseModule
import fuwafuwa.time.bookechi.di.syncModule
import fuwafuwa.time.bookechi.ui.feature.add_book.di.addBookModule
import fuwafuwa.time.bookechi.ui.feature.book_details.di.bookDetailsModule
import fuwafuwa.time.bookechi.ui.feature.book_list.di.bookListModule
import fuwafuwa.time.bookechi.ui.feature.library.di.libraryModule
import fuwafuwa.time.bookechi.ui.feature.onboarding.di.onboardingModule
import fuwafuwa.time.bookechi.ui.feature.productivity.di.productivityModule
import fuwafuwa.time.bookechi.ui.feature.reading_goals.di.readingGoalsModule
import fuwafuwa.time.bookechi.ui.feature.read_shelf.di.readShelfModule
import fuwafuwa.time.bookechi.ui.feature.reading_log.di.readingLogModule
import fuwafuwa.time.bookechi.ui.feature.reading_stats.di.readingStatsModule
import fuwafuwa.time.bookechi.ui.feature.settings.di.settingsModule
import fuwafuwa.time.bookechi.ui.feature.update_progress.di.updateProgressModule
import fuwafuwa.time.bookechi.ui.feature.update_result.di.updateResultModule
import fuwafuwa.time.bookechi.ui.feature.update_result.di.updateResultModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookechiApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        val koinApp = startKoin {
            androidContext(this@BookechiApplication)
            modules(
                databaseModule,
                authModule,
                syncModule,
                onboardingModule,
                bookListModule,
                addBookModule,
                bookDetailsModule,
                productivityModule,
                updateProgressModule,
                updateResultModule,
                libraryModule,
                settingsModule,
                readingLogModule,
                readShelfModule,
            )
        }

        // Тихий анонимный вход на старте: гарантируем наличие аккаунта (uid) для скоупинга данных.
        // Требует включённого Anonymous-провайдера в Firebase Auth.
        val authRepository = koinApp.koin.get<AuthRepository>()
        applicationScope.launch {
            runCatching { authRepository.ensureSignedIn() }
        }

        // Запускаем движок синхронизации Room ↔ Firestore (push по изменениям, realtime pull).
        koinApp.koin.get<SyncManager>().start()
    }
}
