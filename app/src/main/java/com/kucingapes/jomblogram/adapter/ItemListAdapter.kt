/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 10:39 AM
 */

package com.kucingapes.jomblogram.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kucingapes.jomblogram.R
import com.kucingapes.jomblogram.view.IButtonDownload
import kotlinx.android.synthetic.main.item_list_image.view.*

class ItemListAdapter(private val images: MutableList<String>,
                      private val videos: MutableList<String>,
                      private val btnDownload: IButtonDownload) : RecyclerView.Adapter<ItemListAdapter.Holder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        context = parent.context
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_list_image, parent, false))
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = images[position]
        val video = videos[position]

        Glide.with(context)
                .load(image)
                .into(holder.itemView.item_image)

        /**
         * Send url of image and video to MainActivity
         * in function 'onDownload'
         * */
        btnDownload.onDownload(image, video)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}