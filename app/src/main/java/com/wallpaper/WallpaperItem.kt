package com.wallpaper

import com.nostra13.universalimageloader.core.assist.ImageSize

data class WallpaperItem(var imageLink: String) {
    var mimeType: String? = null
    var imageSize: Int? = null
    var imageDimension: ImageSize? = null
}