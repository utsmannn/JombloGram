/*
 * Created by Muhammad Utsman on 22/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/22/18 2:13 AM
 */

package com.kucingapes.jomblogram.helper

import android.content.Context
import android.support.v4.widget.CircularProgressDrawable

object Utils {

    fun circularProgress(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
}