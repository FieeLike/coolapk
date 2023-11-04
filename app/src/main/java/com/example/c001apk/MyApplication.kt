package com.example.c001apk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.c001apk.util.PrefManager
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.loader.glide.GlideImageLoader
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        AppCompatDelegate.setDefaultNightMode(PrefManager.darkTheme)
        //DynamicColors.applyToActivitiesIfAvailable(this)

        Mojito.initialize(
            GlideImageLoader.with(this),
            SketchImageLoadFactory()
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }



}