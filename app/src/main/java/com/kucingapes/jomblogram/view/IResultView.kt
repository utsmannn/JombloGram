/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 10:39 AM
 */

package com.kucingapes.jomblogram.view

import com.androidnetworking.error.ANError
import com.kucingapes.jomblogram.model.Instagram

interface IResultView {
    fun onResultComplete(instagram: Instagram)
    fun onResultError(anError: ANError)
}