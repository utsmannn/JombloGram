/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 10:39 AM
 */

package com.kucingapes.jomblogram.presenter

import android.content.Context
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.kucingapes.jomblogram.BuildConfig
import com.kucingapes.jomblogram.model.Instagram
import com.kucingapes.jomblogram.view.IResultView

class ResultPresenter(context: Context) {
    private val view = context as IResultView

    /**
     * base api url with dynamic id post
     * */
    private val baseUrl = "https://rest.farzain.com/api/ig_post.php?id={post}"

    /**
     * Call api using Android Networking
     * and parse into Instagram class as model
     * */
    fun getPostInstagram(postId: String) {
        AndroidNetworking.get(baseUrl)
                .addPathParameter("post", postId)
                .addQueryParameter("apikey", BuildConfig.apiKey)
                .build()
                .getAsObject(Instagram::class.java, object : ParsedRequestListener<Instagram> {
                    override fun onResponse(response: Instagram) {
                        /**
                         * return data responses to MainActivity
                         * in function 'onResultComplete'
                         * */
                        view.onResultComplete(response)
                    }

                    override fun onError(anError: ANError) {
                        /**
                         * Return error to MainActivity
                         * in function 'onResultError'
                         * */
                        view.onResultError(anError)
                    }

                })
    }
}