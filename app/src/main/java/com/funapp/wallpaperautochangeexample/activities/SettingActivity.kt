package com.funapp.wallpaperautochangeexample.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.funapp.wallpaperautochangeexample.R

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_setting)
        openNewFragmnet()
    }

    fun openNewFragmnet() = MySettingsFragment().apply {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout , this)
            .commit()
    }
}
