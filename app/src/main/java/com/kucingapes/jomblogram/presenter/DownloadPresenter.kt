/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/21/18 2:53 AM
 */

package com.kucingapes.jomblogram.presenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kucingapes.jomblogram.R
import com.kucingapes.jomblogram.helper.UriProvider
import com.kucingapes.jomblogram.view.IDownloadView
import java.io.File
import java.util.regex.Pattern

class DownloadPresenter(private val context: Context) {
    private val view = context as IDownloadView
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun start(url: String, format: String) {

        /**
         * Setup notification for notify download
         * */
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationBuilder = NotificationCompat.Builder(context, "id").apply {
            setContentTitle("Download")
            setSmallIcon(R.drawable.ic_notif)
            priority = NotificationCompat.PRIORITY_MAX
        }

        /**
         * Set directory download in /Download/JombloGram
         * */
        val dirPath =
            Environment.getExternalStorageDirectory().absolutePath + "/" + Environment.DIRECTORY_DOWNLOADS + "/JombloGram"

        /**
         * Getting "mentahan" string for generated random number
         * */
        val stringName = url.substring(url.length - 50)
            .replace("/", "")
            .replace("-", "")
            .replace(".", "")
            .replace(":", "")
            .replace("?", "")
            .replace("_", "")
            .replace("=", "")

        /**
         * Try generated 'stringName' to number for using in notification id
         * and name of file downloaded
         * */
        val name = try {
            java.lang.Long.parseLong(stringName.subSequence(0, 6).toString(), 36).toInt()
        } catch (e: Exception) {
            23245678
        }

        /**
         * Request permission with Dexter (ehehehe :p)
         * */
        Dexter.withActivity(context as AppCompatActivity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    downloadStart(url, dirPath, name, format)
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Snackbar.make(context.findViewById(R.id.parent), "Permission Denied !!", Snackbar.LENGTH_SHORT)
                        .show()
                }

            })
            .check()
    }


    /**
     * Download service with Android Networking
     * */
    private fun downloadStart(url: String, dirPath: String, name: Int, format: String) {
        AndroidNetworking.download(url, dirPath, "$name.$format")
            .build()
            .setDownloadProgressListener { bytesDownloaded, totalBytes ->

                /**
                 * Setup notification for download progress
                 * */
                notificationManagerCompat.apply {
                    val size = formatLong(totalBytes)

                    notificationBuilder.setContentText(size)
                    notificationBuilder.setProgress(totalBytes.toInt(), bytesDownloaded.toInt(), false)
                    notificationBuilder.setOnlyAlertOnce(true)
                    notify(name, notificationBuilder.build())
                }

                /**
                 * Cancel notification if progress value = total size
                 * */
                if (bytesDownloaded == totalBytes) {
                    notificationManagerCompat.cancel(name)
                }
            }
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {

                    /**
                     * Setup uri provider if needed for scan media downloaded file
                     * */
                    val uriFile = UriProvider.getFileUri(context, File(dirPath, "$name.$format"))
                    val newFile = File(uriFile.path)

                    val mediaScan = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

                    /**
                     * handle error when device need scan with uri provider
                     * */
                    try {
                        mediaScan.data = Uri.fromFile(File(dirPath, "$name.$format"))
                        context.sendBroadcast(mediaScan)
                    } catch (e: Exception) {
                        mediaScan.data = Uri.fromFile(newFile)
                        context.sendBroadcast(mediaScan)
                    }

                    /**
                     * Notify to download complete
                     * */
                    notificationManagerCompat.apply {
                        notificationBuilder.setContentText("Download Complete")
                            .setProgress(0, 0, false)
                        notify(name, notificationBuilder.build())
                    }

                    /**
                     * return function 'onDownloadComplete' to MainActivity
                     * */
                    view.onDownloadComplete()
                }

                override fun onError(anError: ANError) {

                    /**
                     * Notify download error
                     * */
                    notificationManagerCompat.apply {
                        notificationBuilder.setContentTitle("Download Error")
                            .setProgress(0, 0, false)
                        notify(name, notificationBuilder.build())
                    }

                    /**
                     * return function 'onDownloadError' to MainActivity
                     * */
                    view.onDownloadError(anError)
                }

            })
    }


    /**
     * function convert long to string for readable size file
     * source: https://gist.github.com/rderoldan1/3866561
     */
    private fun formatLong(number: Long): String {
        val formatted: String
        when {
            number > 1000000000 -> {
                val re = "^(.*)\\d{9}$"
                val m = Pattern.compile(re).matcher(java.lang.Long.toString(number))
                formatted = if (m.find()) {
                    m.group(1) + " Gb"
                } else {
                    "0"
                }
            }
            number > 1000000 -> {
                val re = "^(.*)\\d{6}$"
                val m = Pattern.compile(re).matcher(java.lang.Long.toString(number))
                formatted = if (m.find()) {
                    m.group(1) + " Mb"
                } else {
                    "0"
                }
            }
            number > 1000 -> {
                val re = "^(.*)\\d{3}$"
                val m = Pattern.compile(re).matcher(java.lang.Long.toString(number))
                formatted = if (m.find()) {
                    m.group(1) + " Kb"
                } else {
                    "0"
                }
            }
            else -> formatted = java.lang.Long.toString(number) + " bytes"
        }
        return formatted

    }
}