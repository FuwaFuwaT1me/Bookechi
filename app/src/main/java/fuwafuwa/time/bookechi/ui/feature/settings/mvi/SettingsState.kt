package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.mvi.api.State

data class SettingsState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "20:00",
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val appVersion: String = "1.0.0",
    val totalBooks: Int = 0,
    val totalReadingSessions: Int = 0,
    val showClearDataDialog: Boolean = false,
    val error: String? = null
) : State

enum class AppLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    RUSSIAN("Русский", "ru"),
    JAPANESE("日本語", "ja")
}

