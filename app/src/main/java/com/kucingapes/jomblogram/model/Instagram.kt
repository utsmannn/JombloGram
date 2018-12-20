package com.kucingapes.jomblogram.model

import com.google.gson.annotations.SerializedName

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