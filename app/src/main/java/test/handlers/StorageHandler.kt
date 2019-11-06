package test.handlers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object StorageHandler {

    /**
     *  Store bitmap in file
     */

    fun storeBitmapInFile(bitmap: Bitmap , file : File){
        try {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , fOut)
            fOut.flush()
            fOut.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    /**
     * Store bitmap with callback
     */

    fun storeBitmapWithCallback(bitmap: Bitmap , file: File , callback: (Boolean) -> Unit){
        try {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , fOut)
            fOut.flush()
            fOut.close()
            callback(true)
        }catch (e:Exception){
            e.printStackTrace()
            callback(false)
        }
    }

    /**
     *  get bitmap from file
     */

    fun getBitmapFromFile(file: File):Bitmap{
        val bmOptions = BitmapFactory.Options()
        return BitmapFactory.decodeFile(file.absolutePath , bmOptions)
    }
}