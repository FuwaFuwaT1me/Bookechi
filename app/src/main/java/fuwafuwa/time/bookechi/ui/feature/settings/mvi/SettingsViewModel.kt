package fuwafuwa.time.bookechi.ui.feature.settings.mvi

import fuwafuwa.time.bookechi.mvi.impl.BaseViewModel

class SettingsViewModel(
    override val model: SettingsModel
) : BaseViewModel<SettingsAction, SettingsState>()

