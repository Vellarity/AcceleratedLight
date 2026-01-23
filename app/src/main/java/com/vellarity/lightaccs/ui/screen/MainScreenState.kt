package com.vellarity.lightaccs.ui.screen

data class MainScreenState(
    val isLight: Boolean,
    val isServiceActive: Boolean,
    val accelerationThreshold: Float = 12.0f,
)