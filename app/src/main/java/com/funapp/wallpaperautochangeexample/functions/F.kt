package com.funapp.wallpaperautochangeexample.functions

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.view.WindowManager

object F {
    fun startWeb(context: Context, string: String){
        context.startActivity(Intent(Intent.ACTION_VIEW , Uri.parse(string)))
    }


    // convert dp - px
    fun dpToPx(dp:Int , context: Context): Int{
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun displayDimensions(context: Context): Point{
        val point = Point()
        val mWindowManager = context.getSystemService(Context.WIFI_SERVICE) as WindowManager
        }
    }
}