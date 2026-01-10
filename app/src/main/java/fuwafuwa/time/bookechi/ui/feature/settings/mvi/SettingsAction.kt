package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.data.preferences.BookListViewType
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface SettingsAction : Action {
    data object LoadSettings : SettingsAction
    
    data class SetDarkMode(val enabled: Boolean) : SettingsAction
    data class SetNotifications(val enabled: Boolean) : SettingsAction
    data class SetReminderTime(val time: String) : SettingsAction
    data class SetLanguage(val language: AppLanguage) : SettingsAction
    
    // Design preferences
    data class SetUseModernDesign(val enabled: Boolean) : SettingsAction
    data class SetBookListViewType(val viewType: BookListViewType) : SettingsAction
    data class SetGridColumns(val columns: Int) : SettingsAction
    
    data class ShowClearDataDialog(val show: Boolean) : SettingsAction
    data object ClearAllData : SettingsAction
    data object ExportData : SettingsAction
}
