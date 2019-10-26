package com.funapp.wallpaperautochangeexample.reusables

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.funapp.wallpaperautochangeexample.BuildConfig
import com.funapp.wallpaperautochangeexample.functions.F
import java.io.File


fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun <T> Context.openNewActivity(it: Class<T>) {
    startActivity(Intent(this, it))
}

fun <T> Context.openNewActivity(it: Class<T>, bundle: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
}


@Throws(Exception::class)
fun String.toFile(context: Context): Uri {
    try {
        return FileProvider.getUriForFile(context, "", toFile())
    } catch (e: Exception) {
        throw e
    }
}


fun Uri.getMime(context: Context): String?{
    val content = context.contentResolver
    return content.getType(this)
}

fun String.toFile(): File {
    return File(this)
}

fun calculateHcf(width: Int , height: Int): Int{
    var width = width
    var height = height

    while (height!=0){
        val t = height
        height = width % height
        width = t
    }
    return width
}

//get display ratio a/b
fun Context.displayRatio(): Pair<Int, Int> {
    /*fun gcd(p: Int, q: Int): Int {
        return if (q == 0) p;
        else gcd(q, p % q);
    }

    val point = F.displayDimensions(this)
    val x = point.x
    val y = point.y
    val gcd = gcd(x, y)

    val a = x / gcd
    val b = y / gcd

    return if (x > y)
        Pair(a, b)
    else
        Pair(b, a)*/

    fun calculateHcf(width1: Int, height1: Int): Int {
        var width = width1
        var height = height1
        while (height != 0) {
            val t = height
            height = width % height
            width = t
        }
        return width
    }

    val point = F.displayDimensions(this)
    val hcf = calculateHcf(point.x, point.y)

    return Pair(point.y / hcf, point.x / hcf)
}

/**
 *  Toast
 */

fun Context.toast(message: String , length : Int = Toast.LENGTH_SHORT){
    Toast.makeText(this , message , length).show()
}

fun SharedPreferences.putAny(name: String , any : Any){
    when(any){
        is String -> edit().putString(name, any).apply()
        is Int -> edit().putInt(name , any).apply()
        is Boolean -> edit().putBoolean(name , any).apply()
    }
}

fun SharedPreferences.remove(name : String){
    edit().remove(name).apply()
}

// log messages
fun logd(message: Any) {
    if (BuildConfig.DEBUG)
        Log.d("wallup", "${Exception().stackTrace[1].className.replace("${BuildConfig.APPLICATION_ID}.", "")} :: $message")
}