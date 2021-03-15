package com.example.companion.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.*
import com.example.companion.ui.activity.ActivityImage
import com.example.companion.ui.components.OverlayTop
import com.example.companion.ui.components.OverlayBack


class MainService : Service() {

    private lateinit var overlayTop: OverlayTop
    private lateinit var overlayBack: OverlayBack
    private lateinit var windowManager: WindowManager

    inner class MBinder() : Binder() {

    }

    override fun onBind(intent: Intent?): IBinder {
        return MBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        init()
        return START_NOT_STICKY
    }

    private fun init() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayBack = OverlayBack(this, windowManager)
        overlayTop = OverlayTop(
            this,
            windowManager,
            overlayBack
        )

        overlayBack.setonClickListenerExit {
            overlayTop.destroy()
            onDestroy()
        }


        overlayBack.setonClickListenerGallery {
            val intent = Intent(this, ActivityImage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.putExtra(ActivityImage.ACTION_CREATE, ActivityImage.ACTION_GALLERY_CODE)
            startActivity(intent)
        }

        overlayBack.setonClickListenerCamera {
            val intent = Intent(this, ActivityImage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            intent.putExtra(ActivityImage.ACTION_CREATE, ActivityImage.ACTION_CAMERA_CODE)
            startActivity(intent)
        }

    }

}
