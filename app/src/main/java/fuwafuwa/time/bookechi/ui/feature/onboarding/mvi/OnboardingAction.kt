package fuwafuwa.time.bookechi.ui.feature.onboarding.mvi

import fuwafuwa.time.bookechi.mvi.api.Action

sealed interface OnboardingAction : Action {
    // Google. idToken добывает UI через Credential Manager при входе в Connecting.
    data object StartGoogle : OnboardingAction
    data class GoogleToken(val idToken: String) : OnboardingAction
    data object GoogleCancelled : OnboardingAction
    data class GoogleFailed(val message: String) : OnboardingAction

    // Навигация по флоу.
    data class OpenEmail(val mode: EmailMode) : OnboardingAction
    data class SwitchEmailMode(val mode: EmailMode) : OnboardingAction
    data object OpenAnon : OnboardingAction
    data object CloseFlow : OnboardingAction
    /** Кнопка «назад» в шапке формы: из recover/sent → signin, иначе закрыть форму. */
    data object EmailBack : OnboardingAction

    // Сабмиты (валидация полей делается в UI; сюда приходят уже валидные значения).
    data class SubmitSignIn(val email: String, val password: String) : OnboardingAction
    data class SubmitRegister(val name: String, val email: String, val password: String) : OnboardingAction
    data class SubmitRecover(val email: String) : OnboardingAction
    data object ContinueAnonymous : OnboardingAction

    data object DismissError : OnboardingAction
    /** Success → «Сменить способ входа» (вернуться к welcome). */
    data object Reset : OnboardingAction
    /** Success → «Открыть библиотеку» (фиксируем прохождение онбординга). */
    data object CompleteOnboarding : OnboardingAction
}
