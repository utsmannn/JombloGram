/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 10:56 PM
 */

package com.kucingapes.jomblogram.view

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.bumptech.glide.Glide
import com.kucingapes.jomblogram.model.Instagram
import com.kucingapes.jomblogram.adapter.ItemListAdapter
import com.kucingapes.jomblogram.R
import com.kucingapes.jomblogram.presenter.DownloadPresenter
import com.kucingapes.jomblogram.presenter.ResultPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content.*
import kotlinx.android.synthetic.main.custom_appbar.*
import kotlinx.android.synthetic.main.info_layout.*

/**
 * Base pattern Model-View-Presenter (MVP)
 * Implement all interface for enable function from presenter
 * */
class MainActivity : AppCompatActivity(), IResultView, IButtonDownload, IDownloadView {

    private lateinit var resultPresenter: ResultPresenter
    private lateinit var downloadPresenter: DownloadPresenter

    private lateinit var imgAdapter: ItemListAdapter
    private val images: MutableList<String> = mutableListOf()
    private val videos: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidNetworking.initialize(this)
        registerNotificationChannel()

        imgAdapter = ItemListAdapter(images, videos, this)
        resultPresenter = ResultPresenter(this)
        downloadPresenter = DownloadPresenter(this)
        initPasting()
        initInfo()
    }

    /**
     * Paste url instagram when app launch
     * */
    private fun initPasting() {
        /**
         * Get data paste from clipboard
         * */
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipBoard.primaryClip

        /**
         * If data paste found
         * */
        if (clipData != null) {
            val item = clipData.getItemAt(0).text.toString()

            /**
             * Case if data paste as Instagram link,
             * start call API in presenter
             * */
            if (item.contains("https://www.instagram.com/")) {
                paste_string.text = item
                card_post.visibility = View.VISIBLE
                tutor_view.visibility = View.GONE
                resultPresenter.getPostInstagram(item)
            } else {
                paste_string.text = getString(R.string.not_ig_link)
            }

        } else {
            paste_string.text = getString(R.string.not_pasting)
        }
    }

    override fun onResultComplete(instagram: Instagram) {
        btn_download.visibility = View.VISIBLE
        btn_copy_caption.visibility = View.VISIBLE
        btn_copy_caption.text = getString(R.string.copy_caption)


        /**
         * Detect if multiple image post,
         * when exists images, add images to list
         * and setup recyclerview
         * */
        if (instagram.pictUrl?.size != null) {
            img_result.visibility = View.GONE
            images.addAll(instagram.pictUrl as MutableList<String>)
            initListImage()

            /**
             * Detect if multiple image post contains video url
             * when exists video, add videos to list
             * */
            if (instagram.videoUrl?.size != null) {
                videos.addAll(instagram.videoUrl as MutableList<String>)
            }

        } else {
            /**
             * if single image or video, direct load with Glide
             * and setup method 'onDownload'
             * */
            Glide.with(this)
                    .load(instagram.firstPict)
                    .into(img_result)

            onDownload(instagram.firstPict as String, null)

            if (instagram.firstVideo != null) {
                onDownload(instagram.firstVideo as String, instagram.firstVideo as String)
            }
        }

        /**
         * Detect caption Instagram from API
         * */
        if (instagram.caption == null || instagram.caption == "") {
            caption.text = getString(R.string.no_caption)
            btn_copy_caption.isEnabled = false
        } else {
            caption.text = instagram.caption
        }

        btn_copy_caption.setOnClickListener {

            /**
             * Copy caption with ClipboardManager
             * */
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("caption", instagram.caption)
            clipboardManager.primaryClip = clipData
            snackBarShow("Caption copied")
        }

        card_caption.visibility = View.VISIBLE
        progress_circular.visibility = View.GONE
    }

    private fun initListImage() {
        list_image.visibility = View.VISIBLE
        indicator.visibility = View.VISIBLE

        list_image.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list_image.adapter = imgAdapter
        imgAdapter.notifyDataSetChanged()

        /**
         * Setup indicator and snap recyclerview
         * */
        indicator.attachTo(list_image)
        LinearSnapHelper().attachToRecyclerView(list_image)

        /**
         * Apply notify changes when recyclerview scrolling
         * notify changes using in func 'onDownload' from IDownloadButton
         * for detect visible item, images or videos
         * */
        list_image.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                imgAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * For error message from call API
     * */
    @SuppressLint("SetTextI18n")
    override fun onResultError(anError: ANError) {
        caption.text = "Error: ${anError.localizedMessage}"
        card_post.visibility = View.GONE
        card_caption.visibility = View.VISIBLE
    }

    override fun onDownload(image: String, video: String?) {
        if (video != null) {
            btn_download.text = getString(R.string.dl_video)
            btn_download.setOnClickListener {
                snackBarShow("Downloads start")
                downloadPresenter.start(video, "mp4")
            }
        } else {

            if (image.contains(".gif")) {
                btn_download.text = getString(R.string.dl_gif)
                btn_download.setOnClickListener {
                    snackBarShow("Download start")
                    downloadPresenter.start(image, "gif")
                }
            } else {
                btn_download.text = getString(R.string.dl_image)
                btn_download.setOnClickListener {
                    snackBarShow("Download start")
                    downloadPresenter.start(image, "jpg")
                }
            }
        }
    }

    override fun onDownloadComplete() {
        snackBarShow("Download complete")
    }

    override fun onDownloadError(anError: ANError) {
        snackBarShow("Download error: ${anError.localizedMessage}")
    }


    /**
     * For Android O+, notificationManager required
     * register notification when app launch
     * */
    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Image"
            val descriptionText = "Notification when download image"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("id", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun snackBarShow(data: String) {
        Snackbar.make(parent_layout, data, Snackbar.LENGTH_SHORT).show()
    }


    /**
     * Setup simple info app with BottomSheetDialog
     * */
    private fun initInfo() {
        val viewInfo = BottomSheetDialog(this)
        viewInfo.setContentView(R.layout.info_layout)

        val btnRepo = viewInfo.btn_repo
        val btnFaaraz = viewInfo.btn_api
        val btnContact = viewInfo.btn_contact

        btnRepo.setOnClickListener {
            val url = "https://github.com/utsmannn/jomblogram"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        btnFaaraz.setOnClickListener {
            val url = "https://rest.farzain.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        btnContact.setOnClickListener {
            val uri = "mailto:utsmannn@outlook.com"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(uri)
            startActivity(intent)
        }

        info.setOnClickListener {
            viewInfo.show()
        }
    }
}