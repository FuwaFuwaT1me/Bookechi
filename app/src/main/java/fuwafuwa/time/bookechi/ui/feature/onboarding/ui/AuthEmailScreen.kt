package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.EmailMode
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingAction
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import fuwafuwa.time.bookechi.ui.theme.LoraFontFamily

private val EmailRegex = Regex("^\\S+@\\S+\\.\\S+$")

@Composable
fun AuthEmailScreen(
    emailMode: EmailMode,
    isBusy: Boolean,
    errorMessage: String?,
    sentToEmail: String,
    onAction: (OnboardingAction) -> Unit,
) {
    val colors = BookechiTheme.colors
    val title = when (emailMode) {
        EmailMode.SignIn -> "Вход"
        EmailMode.Register -> "Создать аккаунт"
        EmailMode.Recover -> "Сброс пароля"
        EmailMode.Sent -> "Письмо отправлено"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.canvas)
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 18.dp)) {
            IconCircleButton(onClick = { onAction(OnboardingAction.EmailBack) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Назад", tint = colors.textPrimary, modifier = Modifier.size(22.dp))
            }
            Text(
                text = title,
                fontFamily = LoraFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
            )
            BrandMark(size = 36.dp)
        }

        when (emailMode) {
            EmailMode.SignIn, EmailMode.Register -> SignInRegister(emailMode, isBusy, errorMessage, onAction)
            EmailMode.Recover -> RecoverForm(isBusy, errorMessage, onAction)
            EmailMode.Sent -> SentState(sentToEmail, onAction)
        }
    }
}

@Composable
private fun SignInRegister(
    mode: EmailMode,
    isBusy: Boolean,
    errorMessage: String?,
    onAction: (OnboardingAction) -> Unit,
) {
    val isRegister = mode == EmailMode.Register
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var passErr by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val e = if (!EmailRegex.matches(email.trim())) "Проверьте адрес — кажется, есть опечатка" else null
        val p = if (pass.length < 6) "Минимум 6 символов" else null
        emailErr = e; passErr = p
        if (e != null || p != null) return
        if (isRegister) onAction(OnboardingAction.SubmitRegister(name, email, pass))
        else onAction(OnboardingAction.SubmitSignIn(email, pass))
    }

    SegmentControl(
        leftActive = !isRegister,
        leftText = "Вход",
        rightText = "Регистрация",
        onLeft = { onAction(OnboardingAction.SwitchEmailMode(EmailMode.SignIn)) },
        onRight = { onAction(OnboardingAction.SwitchEmailMode(EmailMode.Register)) },
        modifier = Modifier.padding(bottom = 22.dp),
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (isRegister) {
            AuthField(label = "Имя", value = name, onValueChange = { name = it }, placeholder = "Как к вам обращаться")
        }
        AuthField(
            label = "Электронная почта",
            value = email,
            onValueChange = { email = it; emailErr = null },
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email,
            error = emailErr,
        )
        AuthPasswordField(
            value = pass,
            onValueChange = { pass = it; passErr = null },
            error = passErr,
            hint = if (isRegister) "Минимум 6 символов" else null,
        )

        if (!isRegister) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                AuthTextLink(text = "Забыли пароль?", onClick = { onAction(OnboardingAction.SwitchEmailMode(EmailMode.Recover)) })
            }
        }

        if (errorMessage != null) {
            Text(text = errorMessage, color = BookechiTheme.colors.accentDeep, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        AuthFilledButton(
            text = if (isRegister) "Создать аккаунт" else "Войти",
            onClick = { submit() },
            busy = isBusy,
        )
    }

    if (isRegister) {
        AuthLegal(prefix = "Регистрируясь, вы принимаете ", modifier = Modifier.padding(top = 16.dp))
    }

    AuthOrDivider(modifier = Modifier.padding(vertical = 22.dp))
    GoogleOutlineButton(text = "Продолжить с Google", onClick = { onAction(OnboardingAction.StartGoogle) }, enabled = !isBusy)

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isRegister) "Уже есть аккаунт? " else "Ещё нет аккаунта? ",
            color = BookechiTheme.colors.textSecondary,
            fontSize = 13.sp,
        )
        AuthTextLink(
            text = if (isRegister) "Войти" else "Создать",
            onClick = { onAction(OnboardingAction.SwitchEmailMode(if (isRegister) EmailMode.SignIn else EmailMode.Register)) },
        )
    }
}

@Composable
private fun RecoverForm(isBusy: Boolean, errorMessage: String?, onAction: (OnboardingAction) -> Unit) {
    val colors = BookechiTheme.colors
    var email by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Укажите почту от аккаунта — пришлём ссылку, чтобы задать новый пароль.",
            color = colors.textSecondary,
            fontSize = 15.5.sp,
            lineHeight = 21.sp,
        )
        AuthField(
            label = "Электронная почта",
            value = email,
            onValueChange = { email = it; emailErr = null },
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email,
            error = emailErr,
        )
        if (errorMessage != null) {
            Text(text = errorMessage, color = colors.accentDeep, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        AuthFilledButton(
            text = "Отправить ссылку",
            onClick = {
                if (!EmailRegex.matches(email.trim())) emailErr = "Укажите почту, на которую отправить ссылку"
                else onAction(OnboardingAction.SubmitRecover(email))
            },
            busy = isBusy,
        )
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AuthTextLink(text = "Вернуться ко входу", onClick = { onAction(OnboardingAction.SwitchEmailMode(EmailMode.SignIn)) })
        }
    }
}

@Composable
private fun SentState(email: String, onAction: (OnboardingAction) -> Unit) {
    val colors = BookechiTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.MailOutline, contentDescription = null, tint = colors.accentDeep, modifier = Modifier.size(34.dp))
        }
        Text(
            text = "Проверьте почту",
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 21.sp,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 18.dp),
        )
        Text(
            text = "Отправили ссылку для сброса на $email. Перейдите по ней, чтобы задать новый пароль.",
            color = colors.textSecondary,
            fontSize = 15.5.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp, bottom = 24.dp).widthIn(max = 280.dp),
        )
        OutlineMethodButton(
            text = "Открыть почтовое приложение",
            leading = {},
            onClick = { onAction(OnboardingAction.CloseFlow) },
        )
        AuthTextLink(
            text = "Вернуться ко входу",
            onClick = { onAction(OnboardingAction.SwitchEmailMode(EmailMode.SignIn)) },
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun SegmentControl(
    leftActive: Boolean,
    leftText: String,
    rightText: String,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BookechiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(colors.chipBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        SegmentTab(text = leftText, active = leftActive, onClick = onLeft, modifier = Modifier.weight(1f))
        SegmentTab(text = rightText, active = !leftActive, onClick = onRight, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SegmentTab(text: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(CircleShape)
            .background(if (active) colors.surfaceElevated else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (active) colors.textPrimary else colors.textSecondary,
            fontSize = 14.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun IconCircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val colors = BookechiTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colors.chipBg)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
