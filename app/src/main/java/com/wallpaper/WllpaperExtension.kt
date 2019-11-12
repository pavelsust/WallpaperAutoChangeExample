package com.wallpaper

import android.os.Environment
import test.functions.toFile

object WllpaperExtension{
    var WALLPAPER_PATH = "${Environment.getExternalStorageDirectory().path}/MainWallpaper"

    fun wallpaperMkdr(){
        if (Environment.getExternalStorageDirectory().exists()){
            if (!WALLPAPER_PATH.toFile().exists()){
                WALLPAPER_PATH.toFile().mkdir()
            }
        }
    }

}