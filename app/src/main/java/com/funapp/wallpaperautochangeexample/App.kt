package com.funapp.wallpaperautochangeexample

import android.app.Application
import androidx.preference.PreferenceManager
import test.CACHED
import test.CACHE_NUMBER
import test.Prefs
import test.functions.F
import test.functions.putAny
import java.io.File

class App :Application(){
    override fun onCreate() {
        super.onCreate()

        Prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!Prefs.contains("search")){
            Prefs.putAny("search" , "nature , landscape")
        }

        // default cached image

        if (!Prefs.contains(CACHE_NUMBER)){
            Prefs.putAny(CACHE_NUMBER , "25")
        }

        // make cached dir

        if (!File(filesDir , CACHED).exists()){
            File(filesDir , CACHED).mkdir()
        }

        F.removeDuplicates(filesDir.listFiles().toList())
    }
}