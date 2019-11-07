package test.functions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import test.CACHED
import test.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.lang.Exception
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

object F {

    fun startWeb(context: Context, string: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(string)))
    }


    // convert dp - px
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun displayDimensions(context: Context): Point {
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getSize(point)
        return point
    }

    fun mkdir() {
        if (Environment.getExternalStorageDirectory().exists()) {
            if (!Config.DEFEAULT_DOWNLOAD_PATH.toFile().exists()) {
                Config.DEFEAULT_DOWNLOAD_PATH.toFile().mkdir()
            }
        }
    }

    // get height based on screen width
    fun getDynamicHeight(
        context: Context,
        screenWidth: Int,
        screenHeight: Int,
        width: Int,
        height: Int
    ): Int {
        val h = ((screenWidth - dpToPx(16, context)) * height) / width

        return if (h > (screenHeight - dpToPx(48, context)))
            screenHeight - dpToPx(48, context)
        else
            h
    }

    // generate shortid
    fun shortid(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..10)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }


    fun deleteCached(context: Context, amount: Int) {
        GlobalScope.launch {
            val file = File(context.filesDir, CACHED)

            /**
             * check amount of chched Image
             */

            if (file.listFiles().size > amount) {
                val files = file.listFiles().filter { it.name.contains(".jpg") }.toTypedArray()
                Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)

                /**
                 *  number of Image to delete
                 */

                val deleteCount = files.size - amount

                /**
                 * delete files
                 */
                files.forEachIndexed { index, file ->

                    if (index < deleteCount) {
                        try {
                            file.delete()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun deleteAllCached(context: Context) {
        GlobalScope.launch {

            try {
                File(context.filesDir, CACHED).listFiles().forEach { it.delete() }
                context.filesDir.listFiles().forEach {
                    /**
                     *  only delete files
                     */

                    if (!it.isDirectory) {
                        it.delete()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                Log.d("F", "all cached images removed")
            }
        }
    }

    fun compareBitmaps(bitmap1: Bitmap?, bitmap2: Bitmap?, callback: (Boolean) -> Unit) {

        if (bitmap1 == null || bitmap2 == null) {
            callback(false)
        } else {
            GlobalScope.launch {
                try {
                    callback(bitmap1.sameAs(bitmap2))
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false)
                }

            }
        }
    }


    // verify file size for wallpaper
    fun verifyFileWallpaper(file: File, files: List<File>, callback: (Boolean) -> Unit) {

        var deleted = false

        GlobalScope.launch {
            for (f in files) {

                // check cache directory as well
                if (f.isDirectory) {
                    for (ff in f.listFiles()) {
                        if (file.exists() && ff.exists()) {
                            val fic = calculateMD5(file)
                            val ffc = calculateMD5(ff)

                            if (fic != null && ffc != null && fic == ffc && file.name != ff.name) {
                                file.delete()
                                deleted = true
                                break
                            }
                        }
                    }
                } else {

                    if (file.exists() && f.exists()) {
                        val fic = calculateMD5(file)
                        val fc = calculateMD5(f)

                        if (fic != null && fc != null && fic == fc && file.name != f.name) {
                            file.delete()
                            deleted = true
                            break
                        }
                    }
                }
            }
            callback(deleted)
        }


    }


    // remove duplicates
    fun removeDuplicates(files: List<File>) {
        GlobalScope.launch {
            try {
                for (f in files) {
                    if (f.isDirectory) {
                        for (ff in f.listFiles())
                            verifyFileWallpaper(ff, f.listFiles().toList()) {

                            }
                    } else
                        verifyFileWallpaper(f, files) {}
                }
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    // md5 of a file
    @SuppressLint("DefaultLocale")
    fun calculateMD5(file: File): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(file.readBytes())
            val digest = md.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in digest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString().toUpperCase()
        } catch (e: Exception) {
            return null
        }
    }

}