/**
 * ISC License
 *
 * Copyright 2018-2019, Saksham (DawnImpulse)
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 **/
package com.funapp.wallpaperautochangeexample.workers

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.funapp.wallpaperautochangeexample.functions.F
import com.funapp.wallpaperautochangeexample.functions.logd
import com.funapp.wallpaperautochangeexample.functions.toast
import com.funapp.wallpaperautochangeexample.handlers.ImageHandler
import com.funapp.wallpaperautochangeexample.handlers.StorageHandler
import test.CACHED
import test.CACHE_NUMBER
import test.Prefs
import test.SHOW_TOAST

import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.util.*

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - develop
 *
 * @note Created on 2019-06-14 by Saksham
 * @note Updates :
 *  Saksham - 2019 08 21 - master - caching for unsplash
 *  Saksham - 2019 09 01 - develop - used normal file.delete with try/catch
 *  Saksham - 2019 09 04 - develop - duplicate image handling
 */
class AutoWallpaper(private val appContext: Context, workerParams: WorkerParameters) : ListenableWorker(appContext, workerParams) {
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var handler: Handler

    // ----------------
    //   start work
    // ----------------
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->

            // remove duplicates
            F.removeDuplicates(appContext.filesDir.listFiles().toList())
            wallpaperManager = WallpaperManager.getInstance(appContext)
            handler = Handler(Looper.getMainLooper())
            wallpaperChange {
                if (it)
                    completer.set(Result.success())
                else
                    completer.set(Result.retry())
            }
        }
    }

    // -----------------------------
    //   wallpaper change handling
    // -----------------------------


    private fun wallpaperChange(callback: (Boolean) -> Unit) {
        val files = appContext.filesDir.listFiles().filter { it.name.contains(".jpg") }.toTypedArray()
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)

        // if < 3 wallpaper available in cache then get more
        if (files.size < 3) {
            // if images available in cache
            if (files.isNotEmpty()) {
                setWallpaper(callback)
                wallpaperCaching(5) {
                    // do nothing for either case since wallpaper is set
                }
            } else {
                // no images available in cache, get some & then set wallpaper
                wallpaperCaching(2) {
                    // after images are fetched
                    val files = appContext.filesDir.listFiles().filter { it.name.contains(".jpg") }

                    // if wallpapers are cached
                    if (files.isNotEmpty())
                        setWallpaper(callback)
                    else
                        callback(false)
                }
            }
        } else
        // get wallpaper for file from dir
            setWallpaper(callback)
    }


    // -------------------
    //   set wallpaper
    // -------------------
    private fun setWallpaper(callback: (Boolean) -> Unit) {
        val files = appContext.filesDir.listFiles().filter { it.name.contains(".jpg") }.toTypedArray()
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR)

        val file = files[0]

        // file found
        if (file.exists()) {
            GlobalScope.launch {
                val bitmap = BitmapFactory.decodeFile(file.path)
                // check if bitmap is not null
                if (bitmap != null) {
                    // set bitmap and increment lastWall
                    wallpaperManager.setBitmap(bitmap)

                    // save bitmap for homescreen cache
                    StorageHandler.storeBitmapInFile(bitmap, File(appContext.cacheDir, "homescreen.jpg"))

                    // save bitmap in cached directory
                    val cached = File(appContext.filesDir, CACHED)
                    StorageHandler.storeBitmapInFile(bitmap, File(cached, "${F.shortid()}.jpg"))

                    // if extra images in cached then delete them
                    F.deleteCached(appContext, Prefs.getString(CACHE_NUMBER, "25")!!.toInt())

                    handler.post {
                        if (Prefs.getBoolean(SHOW_TOAST, true))
                            appContext.toast("wallpaper changed")
                    }

                    // delete the file
                    try {
                        file.delete()
                    } catch (e: Exception) {
                        //Crashlytics.logException(e)
                        e.printStackTrace()
                    }
                    callback(true)

                } else {
                    // bitmap is null
                    // delete the file
                    try {
                        file.delete()
                    } catch (e: Exception) {
                        //Crashlytics.logException(e)
                        e.printStackTrace()
                    }
                    callback(false)
                }
            }
        } else {
            // file not found
            // we need to retry with next wallpaper
            callback(false)
        }
    }

    // ----------------------------
    //   save images to internal
    // ----------------------------
    private fun wallpaperCaching(count: Int, callback: () -> Unit) {
        GlobalScope.launch {

            /*// delay of 3s if there is some images present in wallpaper cache
            if (!appContext.filesDir.listFiles().none { it.name.contains(".jpg") })
                delay(3000)*/

            ImageHandler.getBitmapWallpaper(appContext, "https://source.unsplash.com/random/1440x3040/?${Prefs.getString("search", "")}") {
                // store in files dir
                it?.let {

                    // get recent files
                    val file = File(appContext.filesDir, "${F.shortid()}.jpg")
                    StorageHandler.storeBitmapWithCallback(it, file) {
                        if (it) {
                            F.verifyFileWallpaper(file, appContext.filesDir.listFiles().toList()) {
                                logd(appContext.filesDir.listFiles().filter { it.name.contains(".jpg") }.size)
                                if (it)
                                    wallpaperCaching(count, callback) // if similar image already present (deleted in fn itself)
                                else {
                                    logd("image ${F.shortid()} cached")

                                    // recursive call
                                    if (count - 1 != 0)
                                        wallpaperCaching(count - 1, callback)
                                    else
                                        callback()
                                }
                            }
                        } else
                            wallpaperCaching(count, callback)
                    }
                }
            }
        }
    }
}
