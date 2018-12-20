/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 10:39 AM
 */

package com.kucingapes.jomblogram.model

import com.google.gson.annotations.SerializedName

/**
 * Create model from json api
 * */
data class Instagram(
        @SerializedName("caption")
        var caption: String?,
        @SerializedName("comment")
        var comment: List<String>?,
        @SerializedName("creator")
        var creator: String?,
        @SerializedName("first_pict")
        var firstPict: String?,
        @SerializedName("first_video")
        var firstVideo: Any?,
        @SerializedName("like")
        var like: Int?,
        @SerializedName("pict_url")
        var pictUrl: MutableList<String>?,
        @SerializedName("status")
        var status: Int?,
        @SerializedName("video_url")
        var videoUrl: MutableList<String>?
)