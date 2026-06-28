package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.data.preferences.BookListViewType
import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface SettingsAction : Action {
    data object LoadSettings : SettingsAction
    
    data class SetDarkMode(val enabled: Boolean) : SettingsAction
    data class SetNotifications(val enabled: Boolean) : SettingsAction
    data class SetReminderTime(val time: String) : SettingsAction
    data class SetLanguage(val language: AppLanguage) : SettingsAction

    // Reading reminder («Напоминание о чтении»)
    data class ToggleReminder(val enabled: Boolean) : SettingsAction
    data class UpdateReminderTime(val time: String) : SettingsAction
    
    // Design preferences
    data class SetUseModernDesign(val enabled: Boolean) : SettingsAction
    data class SetBookListViewType(val viewType: BookListViewType) : SettingsAction
    data class SetGridColumns(val columns: Int) : SettingsAction
    
    data class ShowClearDataDialog(val show: Boolean) : SettingsAction
    data object ClearAllData : SettingsAction
    data object ExportData : SettingsAction

    // Account / auth.
    // idToken добывается в UI через Credential Manager, дальше вход обрабатывает модель.
    data object SignInStarted : SettingsAction
    data class SignInWithGoogle(val idToken: String) : SettingsAction
    data object SignInCancelled : SettingsAction
    data class SignInFailed(val message: String) : SettingsAction
    data object SignOut : SettingsAction
    data object DismissAuthError : SettingsAction
}
