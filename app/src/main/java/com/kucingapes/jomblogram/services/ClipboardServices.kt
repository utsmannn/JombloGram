/*
 * Created by Muhammad Utsman on 22/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/22/18 12:45 AM
 */

package com.kucingapes.jomblogram.services

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.kucingapes.jomblogram.view.MainActivity

class ClipboardServices : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        /**
         * Get data paste from clipboard
         * */
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipBoard.primaryClip

        clipBoard.addPrimaryClipChangedListener {

            /**
             * If data paste found
             * */
            if (clipData != null) {
                val item = clipData.getItemAt(0).text.toString()

                /**
                 * Case if data paste as Instagram link,
                 * start call API in presenter
                 * */

                if (item.startsWith("https://www.instagram.com/")) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("com.kucingapes.jomblogram.services.BgService"))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}