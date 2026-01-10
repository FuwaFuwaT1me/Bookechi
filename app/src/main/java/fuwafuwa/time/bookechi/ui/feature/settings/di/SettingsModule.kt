package fuwafuwa.time.bookechi.ui.feature.settings.di

import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsModel
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsState
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)

    factory {
        SettingsState()
    }

    factory {
        SettingsModel(
            defaultState = get<SettingsState>(),
            bookRepository = get<BookRepository>(),
            appPreferences = get<AppPreferences>()
        )
    }
}
