package com.example.companion.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.*
import android.widget.Toast
import com.example.companion.activity.CameraPhotoActivity
import com.example.companion.overlay.ui.OverlayViewTop
import com.example.companion.overlay.ui.OverlayViewBack


class MainBackService : Service() {

    private lateinit var overlayViewTop: OverlayViewTop
    private lateinit var overlayViewBack: OverlayViewBack
    private lateinit var windowManager: WindowManager

    inner class MyBinder() : Binder() {

    }

    override fun onBind(intent: Intent?): IBinder {
        return MyBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        init()
        return START_NOT_STICKY
    }

    private fun init() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        //val display = this.resources.displayMetrics
        overlayViewBack = OverlayViewBack(this, windowManager)
        overlayViewBack.setonClickListenerSettings {
            //Toast.makeText(this, "Click !", Toast.LENGTH_SHORT).show()
        }
        overlayViewBack.setonClickListenerCamera {
            val intent = Intent(this, CameraPhotoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        overlayViewTop = OverlayViewTop(
                this,
                windowManager,
                overlayViewBack
        )
    }

}
