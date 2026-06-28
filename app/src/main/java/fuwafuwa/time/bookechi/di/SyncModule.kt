package fuwafuwa.time.bookechi.di

import com.google.firebase.firestore.FirebaseFirestore
import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.local.BookDao
import fuwafuwa.time.bookechi.data.local.DeletedRecordDao
import fuwafuwa.time.bookechi.data.local.ReadingSessionDao
import fuwafuwa.time.bookechi.data.sync.SyncManager
import org.koin.dsl.module

val syncModule = module {
    single { FirebaseFirestore.getInstance() }

    single {
        SyncManager(
            firestore = get<FirebaseFirestore>(),
            authRepository = get<AuthRepository>(),
            bookDao = get<BookDao>(),
            sessionDao = get<ReadingSessionDao>(),
            deletedDao = get<DeletedRecordDao>(),
        )
    }
}
