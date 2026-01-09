package fuwafuwa.time.bookechi

import android.app.Application
import fuwafuwa.time.bookechi.di.databaseModule
import fuwafuwa.time.bookechi.ui.feature.add_book.di.addBookModule
import fuwafuwa.time.bookechi.ui.feature.book_details.di.bookDetailsModule
import fuwafuwa.time.bookechi.ui.feature.book_list.di.bookListModule
import fuwafuwa.time.bookechi.ui.feature.reading_goals.di.readingGoalsModule
import fuwafuwa.time.bookechi.ui.feature.reading_stats.di.readingStatsModule
import fuwafuwa.time.bookechi.ui.feature.settings.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookechiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@BookechiApplication)
            modules(
                databaseModule,
                bookListModule,
                addBookModule,
                bookDetailsModule,
                readingStatsModule,
                readingGoalsModule,
                settingsModule
            )
        }
    }
}
