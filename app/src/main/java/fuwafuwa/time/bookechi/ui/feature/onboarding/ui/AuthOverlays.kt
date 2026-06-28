package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.AuthVia
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingAction
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LoraFontFamily

/** Полноэкранный оверлей подключения Google. */
@Composable
fun AuthConnectingScreen() {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier.fillMaxSize().background(colors.canvas).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            AuthSpinner(size = 64.dp, color = colors.accent, strokeWidth = 3.dp)
            GoogleG(26.dp)
        }
        Text(
            text = "Подключаем Google…",
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 22.dp),
        )
        Text(
            text = "Секунду — открываем безопасный вход",
            color = colors.textSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** Полноэкранный success-экран с «печатью», копирайтом по способу входа и кнопками. */
@Composable
fun AuthSuccessScreen(
    via: AuthVia,
    onOpenLibrary: () -> Unit,
    onSecondary: () -> Unit,
) {
    val colors = BookechiTheme.colors
    val title = when (via) {
        AuthVia.Google -> "Вы вошли"
        AuthVia.Email -> "Добро пожаловать"
        AuthVia.Anon -> "Готово"
    }
    val sub = when (via) {
        AuthVia.Google -> "Аккаунт Google подключён. Рады видеть вас в Bookechi."
        AuthVia.Email -> "Аккаунт готов — ваша полка ждёт первую отметку."
        AuthVia.Anon -> "Вы зашли без аккаунта. Прогресс хранится на этом устройстве."
    }

    val scale = remember { Animatable(0.45f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.canvas).padding(start = 24.dp, end = 24.dp, bottom = 28.dp)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(colors.accent, colors.accentDeep))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = authOnAccent(), modifier = Modifier.size(42.dp))
            }
            Text(
                text = title,
                fontFamily = LoraFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                color = colors.textPrimary,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                text = sub,
                color = colors.textSecondary,
                fontSize = 15.5.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 11.dp).widthIn(max = 280.dp),
            )
            if (via == AuthVia.Anon) {
                Row(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.chipBg)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(17.dp))
                    Text(
                        text = "Данные на этом устройстве",
                        color = colors.textSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 9.dp),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            AuthFilledButton(text = "Открыть библиотеку", onClick = onOpenLibrary)
            Box(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), contentAlignment = Alignment.Center) {
                AuthTextLink(
                    text = if (via == AuthVia.Anon) "Создать аккаунт" else "Сменить способ входа",
                    onClick = onSecondary,
                    color = if (via == AuthVia.Anon) colors.accentDeep else colors.textSecondary,
                )
            }
        }
    }
}
