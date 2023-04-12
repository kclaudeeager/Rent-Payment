package com.example.rent.di

import com.example.rent.ui.views.LoginActivity
import com.example.rent.ui.views.MainActivity
import com.example.rent.work.CustomWorkerFactory
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: LoginActivity)

    fun workerFactory(): CustomWorkerFactory
}

