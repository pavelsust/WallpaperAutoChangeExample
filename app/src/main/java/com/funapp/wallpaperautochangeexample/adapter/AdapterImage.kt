package com.funapp.wallpaperautochangeexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.funapp.wallpaperautochangeexample.R
import java.io.File

class AdapterImage(val items: List<File>) : RecyclerView.Adapter<HolderImage>() {


    override fun onBindViewHolder(holder: HolderImage, position: Int) {
        holder.setBitmap(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImage {
        return HolderImage(
            LayoutInflater.from(parent.context).inflate(
                R.layout.inflator_image,
                parent,
                false
            )
        )
    }
}