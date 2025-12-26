package com.vellarity.lightaccs.ui.screen

sealed interface MainScreenAction {
    data object ToggleLight: MainScreenAction
}
