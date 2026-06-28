package fuwafuwa.time.bookechi.ui.feature.onboarding.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class OnboardingViewModel(
    override val model: OnboardingModel
) : BaseViewModel<OnboardingAction, OnboardingState>()
