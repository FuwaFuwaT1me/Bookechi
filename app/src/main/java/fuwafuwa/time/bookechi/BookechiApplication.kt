package fuwafuwa.time.bookechi

import android.app.Application
import fuwafuwa.time.bookechi.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookechiApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@BookechiApplication)
            modules(databaseModule)
        }
    }
}
