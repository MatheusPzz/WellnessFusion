package com.example.wellnessfusionapp

import android.app.Application
import com.google.firebase.FirebaseApp

class FBAuthApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        // Now Firebase Auth can be used anywhere after it's been initialized here
    }
}