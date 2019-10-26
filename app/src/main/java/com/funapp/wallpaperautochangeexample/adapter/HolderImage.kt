package com.funapp.wallpaperautochangeexample.adapter

import android.graphics.BitmapFactory
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.inflator_image.view.*
import java.io.File

class HolderImage(view: View): RecyclerView.ViewHolder(view){

    private val image = view.inflatorImage
    private val layout = view.imageLayout
    private val context = view.context

    fun setBitmap(file: File){
        val bitmap = BitmapFactory.decodeFile(file.path)

        bitmap.let {
            
        }
    }
}