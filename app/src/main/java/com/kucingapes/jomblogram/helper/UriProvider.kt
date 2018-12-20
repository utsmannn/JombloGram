/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/20/18 8:51 PM
 */

package com.kucingapes.jomblogram.helper

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import java.io.File

object UriProvider {
    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context,
                "com.kucingapes.jomblogram.fileprovider", file)
    }
}