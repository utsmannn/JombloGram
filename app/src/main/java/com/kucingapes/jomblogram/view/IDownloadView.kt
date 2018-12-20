/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 8:08 PM
 */

package com.kucingapes.jomblogram.view

import com.androidnetworking.error.ANError

interface IDownloadView {
    fun onDownloadComplete()
    fun onDownloadError(anError: ANError)
}