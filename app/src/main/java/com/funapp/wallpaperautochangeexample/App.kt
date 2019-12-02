package com.funapp.wallpaperautochangeexample

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
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

        initImageLoader(applicationContext)
    }


    fun initImageLoader(context: Context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        val config = ImageLoaderConfiguration.Builder(
            context
        )
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(50 * 1024 * 1024) // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.writeDebugLogs() // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }
}