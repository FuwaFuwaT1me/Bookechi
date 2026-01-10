package fuwafuwa.time.bookechi.ui.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.data.preferences.BookListViewType
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsAction
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsState
import fuwafuwa.time.bookechi.ui.feature.settings.mvi.SettingsViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import kotlinx.serialization.Serializable

@Serializable
data object SettingsScreenRoute : Screen

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    SettingsScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF64748B), Color(0xFF475569))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙️",
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Settings",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Customize your experience",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueMain)
                }
            } else {
                // Design Section
                SettingsSection(title = "🎨 Design") {
                    SettingsToggleItem(
                        icon = Icons.Filled.Palette,
                        iconColor = Color(0xFF8B5CF6),
                        title = "Modern Design",
                        subtitle = if (state.useModernDesign) "Using new design" else "Using classic design",
                        isChecked = state.useModernDesign,
                        onCheckedChange = { onAction(SettingsAction.SetUseModernDesign(it)) }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE2E8F0)
                    )
                    
                    // View Type Selector
                    ViewTypeSelector(
                        currentViewType = state.bookListViewType,
                        onViewTypeChange = { onAction(SettingsAction.SetBookListViewType(it)) }
                    )
                    
                    // Grid Columns Selector (only shown when grid view is selected)
                    if (state.bookListViewType == BookListViewType.GRID) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = Color(0xFFE2E8F0)
                        )
                        
                        GridColumnsSelector(
                            currentColumns = state.gridColumns,
                            onColumnsChange = { onAction(SettingsAction.SetGridColumns(it)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingsToggleItem(
                        icon = Icons.Filled.DarkMode,
                        iconColor = Color(0xFF6366F1),
                        title = "Dark Mode",
                        subtitle = "Switch to dark theme",
                        isChecked = state.isDarkMode,
                        onCheckedChange = { onAction(SettingsAction.SetDarkMode(it)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notifications Section
                SettingsSection(title = "Notifications") {
                    SettingsToggleItem(
                        icon = Icons.Filled.Notifications,
                        iconColor = Color(0xFFF59E0B),
                        title = "Daily Reminders",
                        subtitle = "Get reminded to read",
                        isChecked = state.notificationsEnabled,
                        onCheckedChange = { onAction(SettingsAction.SetNotifications(it)) }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE2E8F0)
                    )
                    
                    SettingsClickableItem(
                        icon = Icons.Filled.Schedule,
                        iconColor = Color(0xFF10B981),
                        title = "Reminder Time",
                        subtitle = state.dailyReminderTime,
                        onClick = { /* TODO: Show time picker */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Section
                SettingsSection(title = "Language") {
                    SettingsClickableItem(
                        icon = Icons.Filled.Language,
                        iconColor = Color(0xFF3B82F6),
                        title = "App Language",
                        subtitle = state.selectedLanguage.displayName,
                        onClick = { /* TODO: Show language picker */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Data Section
                SettingsSection(title = "Data & Storage") {
                    SettingsInfoItem(
                        icon = Icons.Filled.Storage,
                        iconColor = Color(0xFF8B5CF6),
                        title = "Library Stats",
                        subtitle = "${state.totalBooks} books in your library"
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE2E8F0)
                    )
                    
                    SettingsClickableItem(
                        icon = Icons.Filled.Delete,
                        iconColor = Color(0xFFEF4444),
                        title = "Clear All Data",
                        subtitle = "Delete all books and reading history",
                        onClick = { onAction(SettingsAction.ShowClearDataDialog(true)) },
                        isDestructive = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About Section
                SettingsSection(title = "About") {
                    SettingsInfoItem(
                        icon = Icons.Filled.Info,
                        iconColor = Color(0xFF64748B),
                        title = "Version",
                        subtitle = state.appVersion
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer
                Text(
                    text = "Made with ❤️ for book lovers",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Clear Data Confirmation Dialog
        if (state.showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { onAction(SettingsAction.ShowClearDataDialog(false)) },
                title = {
                    Text(
                        text = "Clear All Data?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("This will permanently delete all your books and reading history. This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = { onAction(SettingsAction.ClearAllData) }
                    ) {
                        Text(
                            text = "Delete",
                            color = Color(0xFFEF4444)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onAction(SettingsAction.ShowClearDataDialog(false)) }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ViewTypeSelector(
    currentViewType: BookListViewType,
    onViewTypeChange: (BookListViewType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF3B82F6).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.GridView,
                contentDescription = null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Book List View",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Choose how books are displayed",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewTypeChip(
                    icon = Icons.Filled.ViewList,
                    label = "List",
                    isSelected = currentViewType == BookListViewType.LIST,
                    onClick = { onViewTypeChange(BookListViewType.LIST) }
                )
                ViewTypeChip(
                    icon = Icons.Filled.GridView,
                    label = "Grid",
                    isSelected = currentViewType == BookListViewType.GRID,
                    onClick = { onViewTypeChange(BookListViewType.GRID) }
                )
            }
        }
    }
}

@Composable
private fun ViewTypeChip(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) BlueMain else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelected) BlueMain else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun GridColumnsSelector(
    currentColumns: Int,
    onColumnsChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$currentColumns",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Grid Columns",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Books per row: $currentColumns",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onColumnsChange(currentColumns - 1) },
                enabled = currentColumns > 2,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentColumns > 2) Color(0xFFF1F5F9) else Color(0xFFF8FAFC)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    tint = if (currentColumns > 2) Color(0xFF64748B) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(
                onClick = { onColumnsChange(currentColumns + 1) },
                enabled = currentColumns < 5,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentColumns < 5) Color(0xFFF1F5F9) else Color(0xFFF8FAFC)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase",
                    tint = if (currentColumns < 5) Color(0xFF64748B) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BlueMain,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) Color(0xFFEF4444) else Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
        
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenContent(
        state = SettingsState(
            totalBooks = 15,
            isDarkMode = false,
            notificationsEnabled = true,
            useModernDesign = true,
            bookListViewType = BookListViewType.LIST,
            gridColumns = 3
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenGridPreview() {
    SettingsScreenContent(
        state = SettingsState(
            totalBooks = 15,
            useModernDesign = true,
            bookListViewType = BookListViewType.GRID,
            gridColumns = 4
        )
    )
}
