package fuwafuwa.time.bookechi.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseHelper {

    inline fun <reified T : RoomDatabase> createDatabase(
        context: Context,
        databaseClass: Class<T>,
        databaseName: String
    ): T {
        return Room.databaseBuilder(
            context.applicationContext,
            databaseClass,
            databaseName
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
