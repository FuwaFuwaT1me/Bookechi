package fuwafuwa.time.bookechi.data.preferences

data class DesignPreferences(
    val useModernDesign: Boolean = true,
    val bookListViewType: BookListViewType = BookListViewType.LIST,
    val gridColumns: Int = 3
)

