package com.example.nbaviewer

import android.app.Application

class NBAViewerApplication : Application() {

    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var dependencies: DIContainer
    override fun onCreate() {
        super.onCreate()
        dependencies = DIContainerImpl()
    }
}