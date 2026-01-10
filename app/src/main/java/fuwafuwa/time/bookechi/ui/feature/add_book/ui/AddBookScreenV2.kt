package fuwafuwa.time.bookechi.ui.feature.add_book.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import fuwafuwa.time.bookechi.base.ui.book.BookCover
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookAction
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookState
import fuwafuwa.time.bookechi.ui.feature.add_book.mvi.AddBookViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain

@Composable
fun AddBookScreenV2(
    viewModel: AddBookViewModel
) {
    val state by viewModel.model.state.collectAsState()

    AddBookScreenV2Content(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun AddBookScreenV2Content(
    state: AddBookState,
    onAction: (AddBookAction) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAction(AddBookAction.LoadBookCover(it)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEEF2FF),
                        Color(0xFFF8FAFC),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(AddBookAction.NavigateBack) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF64748B)
                    )
                }

                Text(
                    text = "Add Book",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                // Save Button
                IconButton(
                    onClick = { onAction(AddBookAction.SaveBook) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(BlueMain, Color(0xFF6366F1))
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Book Cover Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📷 Book Cover",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cover Preview
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(
                                width = 2.dp,
                                color = Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.bookCoverPath != null) {
                            BookCover(
                                imageUri = state.bookCoverPath.toUri(),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add Cover",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Book Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "📖 Book Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Name
                    StyledTextField(
                        value = state.bookName,
                        onValueChange = { onAction(AddBookAction.UpdateBookName(it)) },
                        label = "Book Title",
                        placeholder = "Enter book title..."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Author
                    StyledTextField(
                        value = state.bookAuthor,
                        onValueChange = { onAction(AddBookAction.UpdateBookAuthor(it)) },
                        label = "Author",
                        placeholder = "Enter author name..."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pages Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "📑 Reading Progress",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Now Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { onAction(AddBookAction.UpdateReadingNow(!state.readingNow)) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Currently Reading",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Track your current page",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Switch(
                            checked = state.readingNow,
                            onCheckedChange = { onAction(AddBookAction.UpdateReadingNow(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BlueMain,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pages Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.readingNow) {
                            StyledTextField(
                                value = if (state.bookCurrentPage > 0) state.bookCurrentPage.toString() else "",
                                onValueChange = { 
                                    it.toIntOrNull()?.let { page -> 
                                        onAction(AddBookAction.UpdateCurrentPage(page)) 
                                    } ?: if (it.isEmpty()) onAction(AddBookAction.UpdateCurrentPage(0)) else onAction(AddBookAction.UpdateCurrentPage(0))
                                },
                                label = "Current Page",
                                placeholder = "0",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        StyledTextField(
                            value = if (state.bookPages > 0) state.bookPages.toString() else "",
                            onValueChange = { 
                                it.toIntOrNull()?.let { pages -> 
                                    onAction(AddBookAction.UpdateAllPages(pages)) 
                                } ?: if (it.isEmpty()) onAction(AddBookAction.UpdateAllPages(0)) else onAction(AddBookAction.UpdateAllPages(0))
                            },
                            label = "Total Pages",
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Progress Preview
                    if (state.readingNow && state.bookPages > 0) {
                        Spacer(modifier = Modifier.height(16.dp))

                        val progress = if (state.bookPages > 0) {
                            (state.bookCurrentPage.toFloat() / state.bookPages * 100).toInt()
                        } else 0

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            BlueMain.copy(alpha = 0.1f),
                                            Color(0xFF6366F1).copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "$progress%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueMain
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFCBD5E1)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueMain,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF8FAFC)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddBookScreenV2Preview() {
    AddBookScreenV2Content(
        state = AddBookState(
            bookName = "The Great Gatsby",
            bookAuthor = "F. Scott Fitzgerald",
            readingNow = true,
            bookPages = 180,
            bookCurrentPage = 45
        ),
        onAction = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AddBookScreenV2EmptyPreview() {
    AddBookScreenV2Content(
        state = AddBookState(
            bookName = "",
            bookAuthor = "",
            readingNow = false,
            bookPages = 0,
            bookCurrentPage = 0
        ),
        onAction = {}
    )
}
