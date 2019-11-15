package com.funapp.wallpaperautochangeexample.adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.funapp.wallpaperautochangeexample.activities.ImageActivity
import test.functions.F
import test.functions.openNewActivity
import test.IMAGE
import kotlinx.android.synthetic.main.inflator_image.view.*
import java.io.File

class HolderImage(view: View): RecyclerView.ViewHolder(view){

    private val image = view.inflatorImage
    private val layout = view.imageLayout
    private val context = view.context

    fun setBitmap(file: File){
        val bitmap = BitmapFactory.decodeFile(file.path)

        bitmap.let {
            // calculating dynamic height

            val point = F.displayDimensions(context)
            val width = point.x/2
            val height = F.getDynamicHeight(context , point.x/2 ,point.y,bitmap.width,bitmap.height)

            //change layout parm based on image

            layout.layoutParams = FrameLayout.LayoutParams(width-F.dpToPx(4, context) , height)
            image.layoutParams = FrameLayout.LayoutParams(width-F.dpToPx(8 , context) , height)

            // set bitmap
            image.setImageBitmap(it)


            // handel image click

            image.setOnClickListener {
                //context.openNewActivity(ImageActivity::class.java){
                    //putString(IMAGE, file.path)
                //}

                Log.d("Message" , "File path: "+file.path)
                ///data/user/0/com.funapp.wallpaperautochangeexample/files/cached/qeQHw6xcPd.jpg
            }
        }
    }
}