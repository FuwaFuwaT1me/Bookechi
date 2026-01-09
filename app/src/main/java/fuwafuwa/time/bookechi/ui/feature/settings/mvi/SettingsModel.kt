package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsModel(
    defaultState: SettingsState,
    private val bookRepository: BookRepository
) : BaseModel<SettingsState, SettingsAction>(defaultState) {

    init {
        loadSettings()
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.LoadSettings -> loadSettings()
            is SettingsAction.SetDarkMode -> updateState { copy(isDarkMode = action.enabled) }
            is SettingsAction.SetNotifications -> updateState { copy(notificationsEnabled = action.enabled) }
            is SettingsAction.SetReminderTime -> updateState { copy(dailyReminderTime = action.time) }
            is SettingsAction.SetLanguage -> updateState { copy(selectedLanguage = action.language) }
            is SettingsAction.ShowClearDataDialog -> updateState { copy(showClearDataDialog = action.show) }
            is SettingsAction.ClearAllData -> handleClearAllData()
            is SettingsAction.ExportData -> handleExportData()
        }
    }

    private fun loadSettings() {
        scope.launch {
            updateState { copy(isLoading = true) }
            
            try {
                val books = bookRepository.getAllBooks().first()
                
                updateState {
                    copy(
                        isLoading = false,
                        totalBooks = books.size,
                        error = null
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun handleClearAllData() {
        scope.launch {
            updateState { copy(isLoading = true, showClearDataDialog = false) }
            
            try {
                bookRepository.deleteAllBooks()
                
                updateState {
                    copy(
                        isLoading = false,
                        totalBooks = 0,
                        totalReadingSessions = 0
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun handleExportData() {
        // TODO: Implement data export functionality
    }
}

