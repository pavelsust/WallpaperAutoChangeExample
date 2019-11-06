package test

import android.content.SharedPreferences
import android.os.Environment

object Config{
    val DEFEAULT_DOWNLOAD_PATH = "${Environment.getExternalStorageDirectory().path}/wallpaper"
}

lateinit var Prefs: SharedPreferences
