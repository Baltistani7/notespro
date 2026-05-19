package com.example.nootepro

import android.app.Application
import com.example.nootepro.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NoteApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android ko batate hain ke context yehi app hai
            androidContext(this@NoteApp)
            // AppModule ko load kiya
            modules(appModule)
        }
    }
}