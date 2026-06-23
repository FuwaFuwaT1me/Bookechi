package fuwafuwa.time.bookechi.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

object DatabaseHelper {

    inline fun <reified T : RoomDatabase> createDatabase(
        context: Context,
        databaseClass: Class<T>,
        databaseName: String,
        vararg migrations: Migration,
    ): T {
        return Room.databaseBuilder(
            context.applicationContext,
            databaseClass,
            databaseName
        )
            .addMigrations(*migrations)
            .fallbackToDestructiveMigration()
            .build()
    }
}
