package fuwafuwa.time.bookechi.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Настройки дизайна приложения
 */
data class DesignPreferences(
    val useModernDesign: Boolean = true,
    val bookListViewType: BookListViewType = BookListViewType.LIST,
    val gridColumns: Int = 3
)

enum class BookListViewType {
    LIST,
    GRID
}

class AppPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _designPreferences = MutableStateFlow(loadDesignPreferences())
    val designPreferences: StateFlow<DesignPreferences> = _designPreferences.asStateFlow()
    
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
    }
}
