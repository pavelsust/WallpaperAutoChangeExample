package com.funapp.wallpaperautochangeexample.reusables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
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

val point = F.di

