package com.vellarity.lightaccs.ui.screen

sealed interface MainScreenAction {
    data object ToggleLight: MainScreenAction
    data class ToggleService(val isActive: Boolean): MainScreenAction
    data class ChangeAccelerationThreshold(val value: Float): MainScreenAction
}
