package fuwafuwa.time.bookechi.di

import fuwafuwa.time.bookechi.data.local.AppDatabase
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DatabaseHelper
import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.data.repository.ReadingSessionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseHelper.createDatabase<AppDatabase>(
            context = androidContext(),
            databaseClass = AppDatabase::class.java,
            databaseName = "bookechi_database"
        )
    }
    
    single<BookDao> {
        get<AppDatabase>().bookDao()
    }
    
    single<ReadingSessionDao> {
        get<AppDatabase>().readingSessionDao()
    }
    
    single {
        BookRepository(
            bookDao = get<BookDao>()
        )
    }
    
    single {
        ReadingSessionRepository(
            readingSessionDao = get<ReadingSessionDao>()
        )
    }
    
    single {
        AppPreferences(androidContext())
    }
}
