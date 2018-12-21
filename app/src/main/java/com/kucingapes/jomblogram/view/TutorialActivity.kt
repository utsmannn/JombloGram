/*
 * Created by Muhammad Utsman on 22/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/22/18 1:02 AM
 */

package com.kucingapes.jomblogram.view

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kucingapes.jomblogram.R
import com.kucingapes.jomblogram.helper.Utils
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(this)
                .setDefaultRequestOptions(RequestOptions().placeholder(Utils.circularProgress(this)))
                .load("https://i.ibb.co/FmcCMK4/20181222-010831.gif")
                .into(gif_tutor)

        btn_close.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("tutorial", true).apply()
    }
}
