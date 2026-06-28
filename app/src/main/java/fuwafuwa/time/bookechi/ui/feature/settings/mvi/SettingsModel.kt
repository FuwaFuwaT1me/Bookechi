package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.data.repository.BookRepository
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsModel(
    defaultState: SettingsState,
    private val bookRepository: BookRepository,
    private val appPreferences: AppPreferences,
    private val authRepository: AuthRepository
) : BaseModel<SettingsState, SettingsAction>(defaultState) {

    init {
        loadSettings()
        observePreferences()
        observeAuthUser()
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.LoadSettings -> loadSettings()
            is SettingsAction.SetDarkMode -> updateState { copy(isDarkMode = action.enabled) }
            is SettingsAction.SetNotifications -> updateState { copy(notificationsEnabled = action.enabled) }
            is SettingsAction.SetReminderTime -> updateState { copy(dailyReminderTime = action.time) }
            // «Напоминание о чтении» — пока только updateState.
            // TODO: persist reminder time/enabled in prefs and schedule a daily notification.
            is SettingsAction.ToggleReminder -> updateState { copy(notificationsEnabled = action.enabled) }
            is SettingsAction.UpdateReminderTime -> updateState { copy(dailyReminderTime = action.time) }
            is SettingsAction.SetLanguage -> updateState { copy(selectedLanguage = action.language) }
            is SettingsAction.SetUseModernDesign -> handleSetModernDesign(action.enabled)
            is SettingsAction.SetBookListViewType -> handleSetBookListViewType(action.viewType)
            is SettingsAction.SetGridColumns -> handleSetGridColumns(action.columns)
            is SettingsAction.ShowClearDataDialog -> updateState { copy(showClearDataDialog = action.show) }
            is SettingsAction.ClearAllData -> handleClearAllData()
            is SettingsAction.ExportData -> handleExportData()
            is SettingsAction.SignInStarted -> updateState { copy(authInProgress = true, authError = null) }
            is SettingsAction.SignInWithGoogle -> handleSignInWithGoogle(action.idToken)
            is SettingsAction.SignInCancelled -> updateState { copy(authInProgress = false) }
            is SettingsAction.SignInFailed -> updateState { copy(authInProgress = false, authError = action.message) }
            is SettingsAction.SignOut -> handleSignOut()
            is SettingsAction.DismissAuthError -> updateState { copy(authError = null) }
        }
    }

    private fun observeAuthUser() {
        scope.launch {
            authRepository.authUser.collect { user ->
                updateState { copy(authUser = user) }
            }
        }
    }

    private fun handleSignInWithGoogle(idToken: String) {
        scope.launch {
            authRepository.signInWithGoogle(idToken).fold(
                onSuccess = { updateState { copy(authInProgress = false, authError = null) } },
                onFailure = { e -> updateState { copy(authInProgress = false, authError = e.message) } },
            )
        }
    }

    private fun handleSignOut() {
        scope.launch {
            updateState { copy(authInProgress = true, authError = null) }
            runCatching { authRepository.signOut() }
            updateState { copy(authInProgress = false) }
        }
    }

    private fun observePreferences() {
        scope.launch {
            appPreferences.designPreferences.collect { prefs ->
                updateState {
                    copy(
                        useModernDesign = prefs.useModernDesign,
                        bookListViewType = prefs.bookListViewType,
                        gridColumns = prefs.gridColumns
                    )
                }
            }
        }
    }

    private fun loadSettings() {
        scope.launch {
            updateState { copy(isLoading = true) }
            
            try {
                val books = bookRepository.getAllBooks().first()
                val designPrefs = appPreferences.designPreferences.value
                
                updateState {
                    copy(
                        isLoading = false,
                        totalBooks = books.size,
                        useModernDesign = designPrefs.useModernDesign,
                        bookListViewType = designPrefs.bookListViewType,
                        gridColumns = designPrefs.gridColumns,
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
    
    private fun handleSetModernDesign(enabled: Boolean) {
        appPreferences.setUseModernDesign(enabled)
    }
    
    private fun handleSetBookListViewType(viewType: fuwafuwa.time.bookechi.data.preferences.BookListViewType) {
        appPreferences.setBookListViewType(viewType)
    }
    
    private fun handleSetGridColumns(columns: Int) {
        appPreferences.setGridColumns(columns)
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
