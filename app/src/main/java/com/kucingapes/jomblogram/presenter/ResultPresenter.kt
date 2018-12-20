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
    private val baseUrl = "https://rest.farzain.com/api/ig_post.php?id={post}"

    fun getPostInstagram(postId: String) {
        AndroidNetworking.get(baseUrl)
                .addPathParameter("post", postId)
                .addQueryParameter("apikey", BuildConfig.apiKey)
                .build()
                .getAsObject(Instagram::class.java, object : ParsedRequestListener<Instagram> {
                    override fun onResponse(response: Instagram) {
                        view.onResultComplete(response)
                    }

                    override fun onError(anError: ANError) {
                        view.onResultError(anError)
                    }

                })
    }
}