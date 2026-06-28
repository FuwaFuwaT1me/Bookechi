package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LoraFontFamily

/** Serif-заголовок (Lora 600) — вордмарка/заголовки авторизации. */
@Composable
fun AuthHeadline(text: String, fontSize: Int, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp,
        letterSpacing = (-0.015 * fontSize).sp,
        color = BookechiTheme.colors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/** Экран приветствия, вариант «Обложки» (прод-дефолт). */
@Composable
fun AuthWelcomeScreen(
    onGoogle: () -> Unit,
    onEmail: () -> Unit,
    onAnon: () -> Unit,
) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Мягкий радиальный фон-герой с веером обложек
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(colors.accentSoft, Color.Transparent),
                            center = Offset(0.5f * 1080f, 0.30f * 600f),
                            radius = 760f,
                        ),
                    )
                    .padding(top = 20.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                CoverFan()
            }

            AuthHeadline(text = "Ваша полка ждёт", fontSize = 29, modifier = Modifier.padding(top = 24.dp))
            Text(
                text = "Соберите книги, которые читаете и любите — Bookechi сохранит каждую страницу.",
                color = colors.textSecondary,
                fontSize = 15.5.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 9.dp).widthIn(max = 272.dp),
            )
        }

        Column(modifier = Modifier.padding(bottom = 22.dp)) {
            AuthMethodStack(
                googleText = "Продолжить с Google",
                emailText = "Войти по почте",
                anonText = "Сначала осмотреться",
                onGoogle = onGoogle,
                onEmail = onEmail,
                onAnon = onAnon,
            )
            AuthLegal(modifier = Modifier.padding(top = 18.dp))
        }
    }
}

/** Стек из трёх способов входа (Google → Почта → Аноним). */
@Composable
fun AuthMethodStack(
    googleText: String,
    emailText: String,
    anonText: String,
    onGoogle: () -> Unit,
    onEmail: () -> Unit,
    onAnon: () -> Unit,
    enabled: Boolean = true,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GoogleMethodButton(text = googleText, onClick = onGoogle, enabled = enabled)
        EmailMethodButton(text = emailText, onClick = onEmail, enabled = enabled)
        AnonMethodButton(text = anonText, onClick = onAnon, enabled = enabled)
    }
}
