package com.example.companion.service.util

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.companion.service.MainService

class Connection(var binder: MainService.MBinder? = null, private val handler: () -> Unit = {}) :
    ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        binder = service as MainService.MBinder
        handler()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

}