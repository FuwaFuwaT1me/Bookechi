package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Поле формы авторизации: label сверху + ввод (height 52, radius 16) + ошибка/хинт снизу. */
@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
    hint: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = BookechiTheme.colors
    var focused by remember { mutableStateOf(false) }
    val borderColor = when {
        error != null -> colors.accentDeep
        focused -> colors.accent
        else -> colors.stroke
    }
    val bg = if (error != null) {
        lerp(if (colors.isDark) colors.surface else colors.surfaceElevated, colors.accentSoft, 0.35f)
    } else {
        if (colors.isDark) colors.surface else colors.surfaceElevated
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 2.dp, bottom = 7.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bg)
                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = colors.textPrimary, fontSize = 16.sp),
                        cursorBrush = SolidColor(colors.accent),
                        visualTransformation = visualTransformation,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focused = it.isFocused },
                    )
                    if (value.isEmpty()) {
                        Text(text = placeholder, color = colors.textSecondary.copy(alpha = 0.7f), fontSize = 16.sp)
                    }
                }
                if (trailing != null) {
                    Box(modifier = Modifier.padding(start = 8.dp)) { trailing() }
                }
            }
        }
        if (error != null) {
            Text(text = error, color = colors.accentDeep, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 2.dp, top = 6.dp))
        } else if (hint != null) {
            Text(text = hint, color = colors.textSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp, top = 6.dp))
        }
    }
}

/** Поле пароля с кнопкой показать/скрыть. */
@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Пароль",
    error: String? = null,
    hint: String? = null,
) {
    val colors = BookechiTheme.colors
    var show by remember { mutableStateOf(false) }
    AuthField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = "••••••••",
        modifier = modifier,
        keyboardType = KeyboardType.Password,
        error = error,
        hint = hint,
        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
        trailing = {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).clickable { show = !show },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (show) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (show) "Скрыть пароль" else "Показать пароль",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        },
    )
}
