package com.vellarity.lightaccs.ui.screen

data class MainScreenState(
    val isLight: Boolean = false,
    val isServiceActive: Boolean = false,
    val accelerationThreshold: Float = 12.0f,
)