/*
 * Created by Muhammad Utsman on 21/12/2018
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 12/21/18 2:53 AM
 */

package com.kucingapes.jomblogram.presenter

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kucingapes.jomblogram.R
import com.kucingapes.jomblogram.model.TypeShare
import com.kucingapes.jomblogram.helper.UriProvider
import com.kucingapes.jomblogram.view.IDownloadView
import java.io.File
import java.util.ArrayList
import java.util.regex.Pattern

class DownloadPresenter(private val context: Context) {
    private val view = context as IDownloadView
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var dialogShare: BottomSheetDialog

    private var shareContent: TypeShare? = null
    private var copyCaption: String? = null

    fun start(url: String, format: String, share: TypeShare?) {
        /**
         * Check if function with share or not
         * */
        shareContent = share

        /**
         * Setup notification for notify download
         * */
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationBuilder = NotificationCompat.Builder(context, "id").apply {
            setContentTitle("Download")
            setSmallIcon(R.drawable.ic_notif)
            priority = NotificationCompat.PRIORITY_MAX
        }

        dialogShare = BottomSheetDialog(context)
        dialogShare.setContentView(R.layout.dialog_share)
        dialogShare.setCancelable(false)

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Dexter.withActivity(context as AppCompatActivity)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            downloadStart(url, dirPath, name, format)
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                            token?.continuePermissionRequest()
                        }
                    }).check()
        }
    }

    fun getCaption(caption: String?) {
        copyCaption = caption
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

                    if (shareContent == TypeShare.REPOST || shareContent == TypeShare.APP) {
                        dialogShare.show()
                    }

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

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriFile)

                        if (url.contains(".jpg") || url.contains(".gif")) {
                            shareIntent.type = "image/*"
                        }
                        if (url.contains(".mp4")) {
                            shareIntent.type = "video/*"
                        }

                        when (shareContent) {
                            TypeShare.REPOST -> {

                                dialogShare.hide()

                                shareIntent.setPackage("com.instagram.android")
                                context.startActivity(shareIntent)

                            }
                            TypeShare.APP -> {
                                dialogShare.hide()
                                createIntentExceptInstagram(shareIntent, uriFile)
                            }
                            else -> {
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
                        }
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

    private fun createIntentExceptInstagram(shareIntent: Intent, uriFile: Uri) {
        val shareIntentsLists = ArrayList<Intent>()
        val resInfos = context.packageManager.queryIntentActivities(shareIntent, 0)
        if (!resInfos.isEmpty()) {
            for (resInfo in resInfos) {
                val packageName = resInfo.activityInfo.packageName
                if (!packageName.toLowerCase().contains("instagram")) {
                    val intent = Intent()
                    intent.component = ComponentName(packageName, resInfo.activityInfo.name)
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_STREAM, uriFile)
                    intent.putExtra(Intent.EXTRA_TEXT, copyCaption)
                    intent.type = "image/*"
                    intent.setPackage(packageName)
                    shareIntentsLists.add(intent)
                }
            }
            if (!shareIntentsLists.isEmpty()) {
                val chooserIntent = Intent.createChooser(shareIntentsLists.removeAt(0), "Choose app to share")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toTypedArray<Parcelable>())
                context.startActivity(chooserIntent)
            } else {
                Log.e("Error", "No Apps can perform your task")
            }

        }
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