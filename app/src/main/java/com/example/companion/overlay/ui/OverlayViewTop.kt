package com.example.companion.overlay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import com.example.companion.R
import com.example.companion.overlay.util.State
import com.example.companion.overlay.util.px

/**
*  It is cloud view !
*/

class OverlayViewTop(
    context: Context,
    private val windowManager: WindowManager,
    private val viewBack: OverlayViewBack
) {
    private val relative: View = View.inflate(context, R.layout.ovarlay_view_top, null)
    private val button: ImageButton = relative.findViewById(R.id.cloud)
    private val params = WindowManager.LayoutParams()

    // Coordinates before the start of the movement
    private var prevMoveX = 0
    private var prevMoveY = 0
    // Coordinates at the start of the movement
    private var startMoveX = 0.0
    private var startMoveY = 0.0

    private val isMoved: Boolean
        get() = prevMoveX != params.x || prevMoveY != params.y

    init {
        initParams()
        setOnTouchListener()
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
        params.flags =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        params.format = PixelFormat.TRANSPARENT
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener() {
        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevMoveX = params.x
                    prevMoveY = params.y
                    startMoveX = event.rawX.toDouble()
                    startMoveY = event.rawY.toDouble()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (viewBack.state == State.CLOSE) {
                        val deltaX = event.rawX - startMoveX
                        val deltaY = event.rawY - startMoveY
                        moveAt(prevMoveX + deltaX.toInt(), prevMoveY + deltaY.toInt())
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!isMoved) {
                        if (viewBack.state == State.CLOSE) {
                            viewBack.open()
                        } else if (viewBack.state == State.OPEN) {
                            viewBack.close()
                        }
                    } else {
                        viewBack.moveAt(params.x, params.y)
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun moveAt(x: Int = 0, y: Int = 0) {
        //Log.e("OverlayViewTop", "x : $x , y : $y")
        if (!inScreen(x, y)) return
        params.x = x
        params.y = y
        windowManager.updateViewLayout(relative, params)
    }

    private fun inScreen(x: Int, y: Int): Boolean {
        return true/*x - 35.px >= 0 &&
                x + 35.px <= params.width &&
                y - 35.px >= 0 &&
                y + 35.px <= params.height*/

    }

    fun destroy() {
        viewBack.destroy()
        windowManager.removeView(relative)
    }
}