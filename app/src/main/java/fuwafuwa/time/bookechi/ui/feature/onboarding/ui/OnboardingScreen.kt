package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.credentials.exceptions.GetCredentialCancellationException
import fuwafuwa.time.bookechi.data.auth.GoogleSignIn
import fuwafuwa.time.bookechi.mvi.ui.Screen
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.AuthFlow
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.AuthVia
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.EmailMode
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingAction
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingViewModel
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingScreenRoute : Screen

private const val PUSH_MS = 340
private const val SHEET_MS = 380
private const val FADE_MS = 360
private val SheetEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0.28f, 1f)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOpenLibrary: () -> Unit,
) {
    val state by viewModel.model.state.collectAsState()
    val context = LocalContext.current

    // Вход в Connecting → добываем Google idToken через Credential Manager.
    LaunchedEffect(state.flow) {
        if (state.flow == AuthFlow.Connecting) {
            GoogleSignIn.getIdToken(context).fold(
                onSuccess = { viewModel.sendAction(OnboardingAction.GoogleToken(it)) },
                onFailure = { e ->
                    if (e is GetCredentialCancellationException) {
                        viewModel.sendAction(OnboardingAction.GoogleCancelled)
                    } else {
                        viewModel.sendAction(OnboardingAction.GoogleFailed(e.message ?: "Не удалось войти через Google"))
                    }
                },
            )
        }
    }

    val colors = BookechiTheme.colors
    Box(modifier = Modifier.fillMaxSize().background(colors.canvas)) {
        // База — welcome
        AuthWelcomeScreen(
            onGoogle = { viewModel.sendAction(OnboardingAction.StartGoogle) },
            onEmail = { viewModel.sendAction(OnboardingAction.OpenEmail(EmailMode.SignIn)) },
            onAnon = { viewModel.sendAction(OnboardingAction.OpenAnon) },
        )

        // Форма почты — push справа
        AnimatedVisibility(
            visible = state.flow == AuthFlow.Email,
            enter = slideInHorizontally(tween(PUSH_MS, easing = SheetEasing)) { it },
            exit = slideOutHorizontally(tween(PUSH_MS, easing = SheetEasing)) { it },
        ) {
            AuthEmailScreen(
                emailMode = state.emailMode,
                isBusy = state.isBusy,
                errorMessage = state.errorMessage,
                sentToEmail = state.sentToEmail,
                onAction = viewModel::sendAction,
            )
        }

        // Аноним — скрим + bottom sheet
        AnimatedVisibility(
            visible = state.flow == AuthFlow.Anon,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
        ) {
            val source = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f))
                    .clickable(interactionSource = source, indication = null) {
                        viewModel.sendAction(OnboardingAction.CloseFlow)
                    },
            )
        }
        AnimatedVisibility(
            visible = state.flow == AuthFlow.Anon,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(SHEET_MS, easing = SheetEasing)) { it },
            exit = slideOutVertically(tween(SHEET_MS, easing = SheetEasing)) { it },
        ) {
            Box(modifier = Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))) {
                AuthAnonSheetContent(onAction = viewModel::sendAction)
            }
        }

        // Connecting (Google)
        AnimatedVisibility(
            visible = state.flow == AuthFlow.Connecting,
            enter = fadeIn(tween(FADE_MS)),
            exit = fadeOut(tween(FADE_MS)),
        ) {
            AuthConnectingScreen()
        }

        // Success
        AnimatedVisibility(
            visible = state.flow == AuthFlow.Done,
            enter = fadeIn(tween(FADE_MS)),
            exit = fadeOut(tween(FADE_MS)),
        ) {
            AuthSuccessScreen(
                via = state.via ?: AuthVia.Google,
                onOpenLibrary = {
                    viewModel.sendAction(OnboardingAction.CompleteOnboarding)
                    onOpenLibrary()
                },
                onSecondary = {
                    if (state.via == AuthVia.Anon) {
                        viewModel.sendAction(OnboardingAction.OpenEmail(EmailMode.Register))
                    } else {
                        viewModel.sendAction(OnboardingAction.Reset)
                    }
                },
            )
        }
    }
}
