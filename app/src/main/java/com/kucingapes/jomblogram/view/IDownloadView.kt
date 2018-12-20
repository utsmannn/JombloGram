package com.kucingapes.jomblogram.view

import com.androidnetworking.error.ANError

interface IDownloadView {
    fun onDownloadComplete()
    fun onDownloadError(anError: ANError)
}