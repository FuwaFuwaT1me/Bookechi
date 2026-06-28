package fuwafuwa.time.bookechi.ui.feature.onboarding.mvi

import fuwafuwa.time.bookechi.mvi.api.State

/** Верхнеуровневый экран state-машины авторизации. */
enum class AuthFlow { Welcome, Email, Anon, Connecting, Done }

/** Каким способом пользователь вошёл (для копирайта success-экрана). */
enum class AuthVia { Google, Email, Anon }

/** Режим экрана почты. */
enum class EmailMode { SignIn, Register, Recover, Sent }

data class OnboardingState(
    val flow: AuthFlow = AuthFlow.Welcome,
    val via: AuthVia? = null,
    val emailMode: EmailMode = EmailMode.SignIn,
    /** Идёт сетевой запрос (спиннер на главной кнопке / connecting-оверлее). */
    val isBusy: Boolean = false,
    /** Ошибка авторизации (неверные данные, сеть) — показывается баннером в форме. */
    val errorMessage: String? = null,
    /** Почта, на которую отправлена ссылка сброса (для экрана «Письмо отправлено»). */
    val sentToEmail: String = "",
) : State
