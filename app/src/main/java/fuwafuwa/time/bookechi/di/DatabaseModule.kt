package fuwafuwa.time.bookechi.di

import fuwafuwa.time.bookechi.data.local.AppDatabase
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DatabaseHelper
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
}
