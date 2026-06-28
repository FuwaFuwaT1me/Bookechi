package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.base.ui.ds.DsShapes
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.EmailMode
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingAction
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LoraFontFamily

/** Содержимое bottom-sheet «Зайти без аккаунта» с честными ограничениями. */
@Composable
fun AuthAnonSheetContent(onAction: (OnboardingAction) -> Unit) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.canvas)
            .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
    ) {
        // grabber
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 2.dp)
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.divider),
        )

        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(colors.accentSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = colors.accentDeep, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(start = 13.dp)) {
                Text(
                    text = "Зайти без аккаунта",
                    fontFamily = LoraFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    color = colors.textPrimary,
                )
                Text(
                    text = "Можно начать прямо сейчас — вот что важно знать",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        Column(modifier = Modifier.padding(top = 6.dp, bottom = 22.dp)) {
            LimitRow(
                icon = Icons.Outlined.CloudOff,
                warn = true,
                title = "Нет синхронизации и бэкапа",
                subtitle = "Книги и серия чтения хранятся только на этом телефоне.",
                showDivider = false,
            )
            LimitRow(
                icon = Icons.Outlined.Refresh,
                warn = true,
                title = "Прогресс не перенесётся",
                subtitle = "При удалении приложения или смене устройства данные потеряются.",
                showDivider = true,
            )
            LimitRow(
                icon = Icons.Outlined.AutoAwesome,
                warn = false,
                title = "Аккаунт можно создать позже",
                subtitle = "Всё, что уже прочитано, сохранится и переедет в облако.",
                showDivider = true,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AuthFilledButton(text = "Продолжить без аккаунта", onClick = { onAction(OnboardingAction.ContinueAnonymous) })
            AuthSecondaryButton(text = "Лучше создать аккаунт", onClick = { onAction(OnboardingAction.OpenEmail(EmailMode.Register)) })
        }
    }
}

@Composable
private fun LimitRow(
    icon: ImageVector,
    warn: Boolean,
    title: String,
    subtitle: String,
    showDivider: Boolean,
) {
    val colors = BookechiTheme.colors
    if (showDivider) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.divider))
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), verticalAlignment = Alignment.Top) {
        val tileBg = if (warn) errorBg() else colors.sageSoft
        val tint = if (warn) errorColor() else colors.sage
        Box(
            modifier = Modifier.size(38.dp).clip(DsShapes.cover).background(tileBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f).padding(start = 13.dp)) {
            Text(text = title, color = colors.textPrimary, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = colors.textSecondary, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** --error-bg / --error из дизайн-системы (нет отдельного токена в BookechiColors). */
@Composable
private fun errorBg(): Color = if (BookechiTheme.colors.isDark) Color(0xFF432A20) else Color(0xFFF3DCD2)

@Composable
private fun errorColor(): Color = if (BookechiTheme.colors.isDark) Color(0xFFE08465) else Color(0xFFA8402A)
