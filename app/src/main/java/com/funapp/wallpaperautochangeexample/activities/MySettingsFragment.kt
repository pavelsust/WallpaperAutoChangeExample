package com.funapp.wallpaperautochangeexample.activities

import android.content.DialogInterface
import android.os.Bundle
import androidx.preference.*
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.funapp.wallpaperautochangeexample.BuildConfig
import test.Prefs
import com.funapp.wallpaperautochangeexample.R
import com.funapp.wallpaperautochangeexample.functions.F
import com.funapp.wallpaperautochangeexample.functions.putAny
import com.funapp.wallpaperautochangeexample.functions.remove
import com.funapp.wallpaperautochangeexample.functions.toast
import com.funapp.wallpaperautochangeexample.handlers.DialogHandler
import com.funapp.wallpaperautochangeexample.workers.AutoWallpaper
import test.AUTO_WALLPAPER
import test.CACHE_NUMBER
import test.DELETE_CACHE
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


class MySettingsFragment : PreferenceFragmentCompat() , Preference.OnPreferenceChangeListener  {


    private lateinit var wallpaperStatus: SwitchPreference
    private lateinit var wallpaperInterval: ListPreference
    private lateinit var wallpaperWifi: SwitchPreference
    private lateinit var crashlytics: SwitchPreference
    private lateinit var analytics: SwitchPreference
    private lateinit var wallpaperChange: SwitchPreference
    private lateinit var search : EditTextPreference
    private lateinit var cacheNumber: ListPreference
    private lateinit var cacheClear: Preference



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        wallpaperStatus = findPreference("wallStatus")!!
        wallpaperInterval = findPreference("wallInterval")!!
        wallpaperWifi = findPreference("wallWifi")!!
        crashlytics = findPreference("crashlytics")!!
        analytics = findPreference("analytics")!!
        wallpaperChange = findPreference("wallChange")!!
        search = findPreference("search")!!
        cacheNumber = findPreference(CACHE_NUMBER)!!
        cacheClear = findPreference(DELETE_CACHE)!!

        findPreference<Preference>("version")!!.summary = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        if (Prefs.getString("search" , "")!!.isEmpty()){
            search.summary = "(No search term, will show random images)"
        }else{
            search.summary = Prefs.getString("search" , "")
        }

        //cachde number of images
        cacheNumber.summary = "Caching upto ${Prefs.getString(CACHE_NUMBER, "25")} images"

        // listeners

        wallpaperStatus.onPreferenceChangeListener = this
        wallpaperInterval.onPreferenceChangeListener = this
        wallpaperWifi.onPreferenceChangeListener = this
        search.onPreferenceChangeListener = this
        cacheNumber.onPreferenceChangeListener = this


        // clear cache listener

        cacheClear.setOnPreferenceClickListener {
            DialogHandler.simpleOk(context!! , "Clear cache", "Wish to clear all images in cache?" , DialogInterface.OnClickListener { _, i ->
                F.deleteAllCached(context!!)
            })
            true
        }
    }



    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {


        when(preference){

            // wallpaper
            wallpaperStatus -> {
                newValue as Boolean
                if (newValue){
                    setWallpaper()
                }else{
                    WorkManager.getInstance(context!!).cancelWorkById(UUID.fromString(
                        Prefs.getString(
                            AUTO_WALLPAPER, "")))
                    Prefs.remove(AUTO_WALLPAPER)

                }
            }

            // interval
            wallpaperInterval->{
                Prefs.putAny("wallInterval"  , newValue as String)
                setWallpaper()
            }

            wallpaperWifi ->{
                Prefs.putAny("wallWifi" , newValue as Boolean)
                setWallpaper()
            }

            //search
            search ->{
                context!!.toast("will start showing new wallpapers on next refresh")
                val files = context!!.filesDir.listFiles().filter { it.name.contains(".jpg") }.toTypedArray()
                try {
                    files.forEach { it.delete() }
                }catch (e : Exception){
                    e.printStackTrace()
                }

                // change title
                if (newValue.toString().isEmpty()){
                    search.summary = "(no search term, will show random images)"
                }else{
                    search.summary = newValue.toString()
                }
            }

            cacheNumber ->{
                val amount = (newValue as String).toInt()
                F.deleteCached(context!! , amount)
                cacheNumber.summary = "Caching upto $amount images"
            }

        }

        return true
    }



    private fun setWallpaper(){
        if (Prefs.contains(AUTO_WALLPAPER)){
            WorkManager.getInstance(context!!).cancelWorkById(UUID.fromString(Prefs.getString(
                AUTO_WALLPAPER, "")))
            Prefs.remove(AUTO_WALLPAPER)
        }

        val time = Prefs.getString("wallInterval" , "1440")!!.toLong()
        val isWifi = Prefs.getBoolean("wallWifi" , true)

        val builder = Constraints.Builder()
            .setRequiredNetworkType(if (isWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
            .build()

        // set wallpaper worker
        val uploadWorkRequest = PeriodicWorkRequestBuilder<AutoWallpaper>(time , TimeUnit.MINUTES)
            .setConstraints(builder).build()

        WorkManager.getInstance(context!!).enqueue(uploadWorkRequest)
        Prefs.putAny(AUTO_WALLPAPER, uploadWorkRequest.id.toString())

        // interval

        val timing = when(time){
            15.toLong() -> "15 min"
            30.toLong() -> "30 min"
            60.toLong() -> "1 hour"
            180.toLong() -> "3 hours"
            360.toLong() -> "6 hours"
            720.toLong() -> "12 hours"
            1440.toLong() -> "1 day"
            4320.toLong() -> "3 days"

            else -> "7 days"
        }
        wallpaperInterval.summary = "Change every $timing (tap to change)"
        context!!.toast("Please wait few mint for wallapers to download on device")
    }

}