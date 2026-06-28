package fuwafuwa.time.bookechi.ui.feature.onboarding.mvi

import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.mvi.impl.BaseModel
import kotlinx.coroutines.launch

class OnboardingModel(
    defaultState: OnboardingState,
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferences,
) : BaseModel<OnboardingState, OnboardingAction>(defaultState) {

    override fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.StartGoogle ->
                updateState { copy(flow = AuthFlow.Connecting, via = AuthVia.Google, isBusy = true, errorMessage = null) }

            is OnboardingAction.GoogleToken -> signInWithGoogle(action.idToken)

            is OnboardingAction.GoogleCancelled ->
                updateState { copy(flow = AuthFlow.Welcome, via = null, isBusy = false) }

            is OnboardingAction.GoogleFailed ->
                updateState { copy(flow = AuthFlow.Welcome, via = null, isBusy = false, errorMessage = action.message) }

            is OnboardingAction.OpenEmail ->
                updateState { copy(flow = AuthFlow.Email, emailMode = action.mode, isBusy = false, errorMessage = null) }

            is OnboardingAction.SwitchEmailMode ->
                updateState { copy(emailMode = action.mode, errorMessage = null) }

            is OnboardingAction.OpenAnon ->
                updateState { copy(flow = AuthFlow.Anon, isBusy = false, errorMessage = null) }

            is OnboardingAction.CloseFlow ->
                updateState { copy(flow = AuthFlow.Welcome, isBusy = false, errorMessage = null) }

            is OnboardingAction.EmailBack -> handleEmailBack()

            is OnboardingAction.SubmitSignIn -> submitSignIn(action.email, action.password)
            is OnboardingAction.SubmitRegister -> submitRegister(action.name, action.email, action.password)
            is OnboardingAction.SubmitRecover -> submitRecover(action.email)
            is OnboardingAction.ContinueAnonymous -> continueAnonymous()

            is OnboardingAction.DismissError -> updateState { copy(errorMessage = null) }

            is OnboardingAction.Reset ->
                updateState { copy(flow = AuthFlow.Welcome, via = null, emailMode = EmailMode.SignIn, isBusy = false, errorMessage = null) }

            is OnboardingAction.CompleteOnboarding -> appPreferences.setOnboardingCompleted(true)
        }
    }

    private fun handleEmailBack() {
        val mode = currentState().emailMode
        if (mode == EmailMode.Recover || mode == EmailMode.Sent) {
            updateState { copy(emailMode = EmailMode.SignIn, errorMessage = null) }
        } else {
            updateState { copy(flow = AuthFlow.Welcome, isBusy = false, errorMessage = null) }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        scope.launch {
            authRepository.signInWithGoogle(idToken).fold(
                onSuccess = { updateState { copy(flow = AuthFlow.Done, via = AuthVia.Google, isBusy = false) } },
                onFailure = { e -> updateState { copy(flow = AuthFlow.Welcome, via = null, isBusy = false, errorMessage = e.message) } },
            )
        }
    }

    private fun submitSignIn(email: String, password: String) {
        scope.launch {
            updateState { copy(isBusy = true, errorMessage = null) }
            authRepository.signInWithEmail(email, password).fold(
                onSuccess = { updateState { copy(flow = AuthFlow.Done, via = AuthVia.Email, isBusy = false) } },
                onFailure = { e -> updateState { copy(isBusy = false, errorMessage = e.message ?: "Не удалось войти") } },
            )
        }
    }

    private fun submitRegister(name: String, email: String, password: String) {
        scope.launch {
            updateState { copy(isBusy = true, errorMessage = null) }
            authRepository.registerWithEmail(name, email, password).fold(
                onSuccess = { updateState { copy(flow = AuthFlow.Done, via = AuthVia.Email, isBusy = false) } },
                onFailure = { e -> updateState { copy(isBusy = false, errorMessage = e.message ?: "Не удалось создать аккаунт") } },
            )
        }
    }

    private fun submitRecover(email: String) {
        scope.launch {
            updateState { copy(isBusy = true, errorMessage = null) }
            authRepository.sendPasswordReset(email).fold(
                onSuccess = { updateState { copy(isBusy = false, emailMode = EmailMode.Sent, sentToEmail = email.trim()) } },
                onFailure = { e -> updateState { copy(isBusy = false, errorMessage = e.message ?: "Не удалось отправить письмо") } },
            )
        }
    }

    private fun continueAnonymous() {
        scope.launch {
            updateState { copy(isBusy = true, errorMessage = null) }
            runCatching { authRepository.ensureSignedIn() }.fold(
                onSuccess = { updateState { copy(flow = AuthFlow.Done, via = AuthVia.Anon, isBusy = false) } },
                onFailure = { e -> updateState { copy(isBusy = false, errorMessage = e.message ?: "Не удалось войти") } },
            )
        }
    }
}
