package fuwafuwa.time.bookechi.di

import fuwafuwa.time.bookechi.data.auth.AuthRepository
import org.koin.dsl.module

val authModule = module {
    single { AuthRepository() }
}
