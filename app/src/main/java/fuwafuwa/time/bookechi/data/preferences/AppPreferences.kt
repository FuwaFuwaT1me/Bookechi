package fuwafuwa.time.bookechi.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _designPreferences = MutableStateFlow(loadDesignPreferences())
    val designPreferences: StateFlow<DesignPreferences> = _designPreferences.asStateFlow()

    private val systemDark: Boolean = (context.resources.configuration.uiMode and
        android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
        android.content.res.Configuration.UI_MODE_NIGHT_YES

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, systemDark))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _isDarkTheme.value = enabled
    }

    fun toggleDarkTheme() = setDarkTheme(!_isDarkTheme.value)

    private val _reminderEnabled = MutableStateFlow(prefs.getBoolean(KEY_REMINDER_ENABLED, true))
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _reminderTime = MutableStateFlow(prefs.getString(KEY_REMINDER_TIME, "21:00") ?: "21:00")
    val reminderTime: StateFlow<String> = _reminderTime.asStateFlow()

    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
        _reminderEnabled.value = enabled
    }

    fun setReminderTime(time: String) {
        prefs.edit().putString(KEY_REMINDER_TIME, time).apply()
        _reminderTime.value = time
    }
    
    private fun loadDesignPreferences(): DesignPreferences {
        return DesignPreferences(
            useModernDesign = prefs.getBoolean(KEY_USE_MODERN_DESIGN, true),
            bookListViewType = BookListViewType.valueOf(
                prefs.getString(KEY_BOOK_LIST_VIEW_TYPE, BookListViewType.LIST.name) ?: BookListViewType.LIST.name
            ),
            gridColumns = prefs.getInt(KEY_GRID_COLUMNS, 3)
        )
    }
    
    fun setUseModernDesign(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USE_MODERN_DESIGN, enabled).apply()
        _designPreferences.value = _designPreferences.value.copy(useModernDesign = enabled)
    }
    
    fun setBookListViewType(viewType: BookListViewType) {
        prefs.edit().putString(KEY_BOOK_LIST_VIEW_TYPE, viewType.name).apply()
        _designPreferences.value = _designPreferences.value.copy(bookListViewType = viewType)
    }
    
    fun setGridColumns(columns: Int) {
        prefs.edit().putInt(KEY_GRID_COLUMNS, columns).apply()
        _designPreferences.value = _designPreferences.value.copy(gridColumns = columns)
    }
    
    companion object {
        private const val PREFS_NAME = "bookechi_preferences"
        private const val KEY_USE_MODERN_DESIGN = "use_modern_design"
        private const val KEY_BOOK_LIST_VIEW_TYPE = "book_list_view_type"
        private const val KEY_GRID_COLUMNS = "grid_columns"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_TIME = "reminder_time"
    }
}
