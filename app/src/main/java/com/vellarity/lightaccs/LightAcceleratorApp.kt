package com.vellarity.lightaccs

import android.annotation.SuppressLint
import android.app.Application
import com.vellarity.lightaccs.di.AppModule

class LightAcceleratorApp: Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}