package fuwafuwa.time.bookechi.ui.feature.onboarding.di

import fuwafuwa.time.bookechi.data.auth.AuthRepository
import fuwafuwa.time.bookechi.data.preferences.AppPreferences
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingModel
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingState
import fuwafuwa.time.bookechi.ui.feature.onboarding.mvi.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)

    factory { OnboardingState() }

    factory {
        OnboardingModel(
            defaultState = get<OnboardingState>(),
            authRepository = get<AuthRepository>(),
            appPreferences = get<AppPreferences>(),
        )
    }
}
