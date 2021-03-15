package com.example.companion.ui.components

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.companion.R
import com.example.companion.ui.components.util.State
import com.example.companion.ui.components.util.awaitTransitionComplete
import kotlinx.coroutines.*

/**
 *  It is motionLayout view
 */

class OverlayBack(
    context: Context,
    private val windowManager: WindowManager
) {
    private val relative: View = View.inflate(context, R.layout.overlay_view_back, null)
    private val motion: MotionLayout = relative.findViewById(R.id.motion_layout) as MotionLayout
    private val params = WindowManager.LayoutParams()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    var state = State.CLOSE
        private set

    init {
        initParams()
        windowManager.addView(relative, params)
    }

    private fun initParams() {
        val TYPE_OVERLAY = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        params.type = TYPE_OVERLAY
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.format = PixelFormat.TRANSPARENT

    }

    fun open() {
        if (state == State.OPEN) return

        state = State.Animated

        scope.launch {
            motion.setTransition(R.id.open_center_step1)
            motion.transitionToEnd()
            motion.awaitTransitionComplete(R.id.center_step1)
            motion.setTransition(R.id.open_center_step2)
            motion.transitionToEnd()
            motion.awaitTransitionComplete(R.id.center_step2)
            state = State.OPEN
        }

        params.flags =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        windowManager.updateViewLayout(relative, params)
    }

    fun close(handler: () -> Unit = {}) {
        if (state == State.CLOSE) return

        state = State.Animated

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        windowManager.updateViewLayout(relative, params)

        scope.launch {
            motion.setTransition(R.id.close_center)
            motion.transitionToEnd()
            motion.awaitTransitionComplete(R.id.start_center)
            state = State.CLOSE
            handler()
        }
    }

    fun moveAt(x: Int = 0, y: Int = 0) {
        params.x = x
        params.y = y
        windowManager.updateViewLayout(relative, params)
    }


    fun setonClickListenerExit(handler: () -> Unit) {
        motion.findViewById<ImageButton>(R.id.exit).setOnClickListener {
            close(handler)
        }
    }

    fun setonClickListenerGallery(handler: () -> Unit) {
        motion.findViewById<ImageButton>(R.id.data).setOnClickListener {
            close(handler)
        }
    }

    fun setonClickListenerScreen(handler: () -> Unit) {
        motion.findViewById<ImageButton>(R.id.screen).setOnClickListener {
            state = State.Animated
            scope.launch {
                motion.setTransition(R.id.close_center)
                motion.transitionToEnd()
                motion.awaitTransitionComplete(R.id.start_center)
                motion.setTransition(R.id.click_center_screen)
                motion.transitionToEnd()
                motion.awaitTransitionComplete(R.id.center_click_screen)
                state = State.OPENCSREEN
                handler()
            }
        }
    }

    fun setonClickListenerCamera(handler: () -> Unit) {
        motion.findViewById<ImageButton>(R.id.stop).setOnClickListener {
            close(handler)
        }
    }

    fun destroy() {
        scope.cancel()
        windowManager.removeView(relative)
    }
}