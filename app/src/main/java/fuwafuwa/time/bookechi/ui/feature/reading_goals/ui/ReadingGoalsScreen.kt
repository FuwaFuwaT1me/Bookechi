package fuwafuwa.time.bookechi.ui.feature.reading_goals.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsAction
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsState
import fuwafuwa.time.bookechi.ui.feature.reading_goals.mvi.ReadingGoalsViewModel
import fuwafuwa.time.bookechi.ui.theme.BlueMain
import kotlinx.serialization.Serializable

@Serializable
data object ReadingGoalsScreenRoute : Screen

@Composable
fun ReadingGoalsScreen(
    viewModel: ReadingGoalsViewModel
) {
    val state by viewModel.model.state.collectAsState()

    ReadingGoalsScreenContent(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun ReadingGoalsScreenContent(
    state: ReadingGoalsState,
    onAction: (ReadingGoalsAction) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFEF3C7),
                        Color(0xFFFFFBEB),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header with Trophy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFBBF24)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Reading Goals",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                    Text(
                        text = "Track your progress",
                        fontSize = 14.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFBBF24))
                }
            } else {
                // Daily Goal Card
                GoalCard(
                    title = "📖 Daily Pages",
                    subtitle = "Pages to read today",
                    currentValue = state.currentDailyProgress,
                    goalValue = state.dailyPagesGoal,
                    progress = state.dailyProgressPercent,
                    unit = "pages",
                    isEditing = state.isEditingDaily,
                    onEditClick = { onAction(ReadingGoalsAction.SetEditingDaily(true)) },
                    onSaveGoal = { onAction(ReadingGoalsAction.UpdateDailyGoal(it)) },
                    gradientColors = listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
                    backgroundColor = Color(0xFFFDF2F8)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Goal Card
                GoalCard(
                    title = "📚 Weekly Books",
                    subtitle = "Books to finish this week",
                    currentValue = state.currentWeeklyProgress,
                    goalValue = state.weeklyBooksGoal,
                    progress = state.weeklyProgressPercent,
                    unit = "books",
                    isEditing = state.isEditingWeekly,
                    onEditClick = { onAction(ReadingGoalsAction.SetEditingWeekly(true)) },
                    onSaveGoal = { onAction(ReadingGoalsAction.UpdateWeeklyGoal(it)) },
                    gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
                    backgroundColor = Color(0xFFF5F3FF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Yearly Goal Card
                GoalCard(
                    title = "🎯 Yearly Challenge",
                    subtitle = "Books to read this year",
                    currentValue = state.currentYearlyProgress,
                    goalValue = state.yearlyBooksGoal,
                    progress = state.yearlyProgressPercent,
                    unit = "books",
                    isEditing = state.isEditingYearly,
                    onEditClick = { onAction(ReadingGoalsAction.SetEditingYearly(true)) },
                    onSaveGoal = { onAction(ReadingGoalsAction.UpdateYearlyGoal(it)) },
                    gradientColors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
                    backgroundColor = Color(0xFFECFDF5)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Encouragement Card
                EncouragementCard(state)
            }
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    subtitle: String,
    currentValue: Int,
    goalValue: Int,
    progress: Float,
    unit: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveGoal: (Int) -> Unit,
    gradientColors: List<Color>,
    backgroundColor: Color
) {
    var editValue by remember(goalValue) { mutableStateOf(goalValue.toString()) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                
                if (!isEditing) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = gradientColors[0],
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                editValue.toIntOrNull()?.let { onSaveGoal(it) }
                            }
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = gradientColors[0],
                            cursorColor = gradientColors[0]
                        ),
                        label = { Text("Goal ($unit)") }
                    )
                    
                    IconButton(
                        onClick = {
                            editValue.toIntOrNull()?.let { onSaveGoal(it) }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(colors = gradientColors)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Save",
                            tint = Color.White
                        )
                    }
                }
            } else {
                // Progress Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$currentValue / $goalValue",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradientColors[0]
                        )
                        Text(
                            text = unit,
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = gradientColors[0]
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.horizontalGradient(colors = gradientColors)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun EncouragementCard(state: ReadingGoalsState) {
    val message = when {
        state.dailyProgressPercent >= 1f -> "🎉 Amazing! You've hit your daily goal!"
        state.dailyProgressPercent >= 0.5f -> "📖 Halfway there! Keep reading!"
        state.dailyProgressPercent > 0f -> "🌟 Great start! Every page counts!"
        else -> "✨ Start reading to track your progress!"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingGoalsScreenPreview() {
    ReadingGoalsScreenContent(
        state = ReadingGoalsState(
            dailyPagesGoal = 30,
            currentDailyProgress = 15,
            weeklyBooksGoal = 2,
            currentWeeklyProgress = 1,
            yearlyBooksGoal = 24,
            currentYearlyProgress = 8
        )
    )
}
