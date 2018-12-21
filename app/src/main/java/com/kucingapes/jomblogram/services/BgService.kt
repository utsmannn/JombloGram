/*
 * Created by Muhammad Utsman on 22/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/22/18 12:33 AM
 */

package com.kucingapes.jomblogram.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BgService : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, ClipboardServices::class.java))
    }
}