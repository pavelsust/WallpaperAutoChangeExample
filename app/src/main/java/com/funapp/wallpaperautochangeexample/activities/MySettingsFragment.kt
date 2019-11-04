package com.funapp.wallpaperautochangeexample.activities

import android.os.Bundle
import androidx.preference.*
import com.funapp.wallpaperautochangeexample.R


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
        wallpaperWifi = findPreference()
    }



    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return true
    }


}