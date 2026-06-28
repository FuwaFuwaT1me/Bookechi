package fuwafuwa.time.bookechi.di

import fuwafuwa.time.bookechi.data.local.AppDatabase
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DatabaseHelper
import fuwafuwa.time.bookechi.data.local.DeletedRecordDao
import fuwafuwa.time.bookechi.data.local.MIGRATION_1_2
import fuwafuwa.time.bookechi.data.local.MIGRATION_2_3
import fuwafuwa.time.bookechi.data.local.MIGRATION_3_4
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
            databaseName = "bookechi_database",
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
        )
    }

    single<BookDao> {
        get<AppDatabase>().bookDao()
    }

    single<ReadingSessionDao> {
        get<AppDatabase>().readingSessionDao()
    }

    single<DeletedRecordDao> {
        get<AppDatabase>().deletedRecordDao()
    }

    single {
        BookRepository(
            bookDao = get<BookDao>(),
            deletedDao = get<DeletedRecordDao>(),
        )
    }

    single {
        ReadingSessionRepository(
            readingSessionDao = get<ReadingSessionDao>(),
            bookDao = get<BookDao>(),
            deletedDao = get<DeletedRecordDao>(),
        )
    }

    single {
        AppPreferences(androidContext())
    }
}
