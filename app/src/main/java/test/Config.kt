package test

import android.content.SharedPreferences
import android.os.Environment

object Config{
    val DEFEAULT_DOWNLOAD_PATH = "${Environment.getExternalStorageDirectory().path}/wallpaper"
    val PLAY_STORE = "https://play.google.com/store/apps/details?id=com.dawnimpulse.wallup"
}

lateinit var Prefs: SharedPreferences
