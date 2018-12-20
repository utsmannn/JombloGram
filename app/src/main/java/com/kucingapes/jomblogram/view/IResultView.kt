package com.kucingapes.jomblogram.view

import com.androidnetworking.error.ANError
import com.kucingapes.jomblogram.model.Instagram

interface IResultView {
    fun onResultComplete(instagram: Instagram)
    fun onResultError(anError: ANError)
}