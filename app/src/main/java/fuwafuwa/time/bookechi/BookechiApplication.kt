package fuwafuwa.time.bookechi

import android.app.Application
import fuwafuwa.time.bookechi.di.databaseModule
import fuwafuwa.time.bookechi.ui.feature.book_list.di.bookListModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookechiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@BookechiApplication)
            modules(
                databaseModule,
                bookListModule
            )
        }
    }
}
