package com.example.companion.services.util

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.companion.services.MainBackService

class Connection(var binder: MainBackService.MyBinder? = null, private val handler: () -> Unit = {}) :
    ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        binder = service as MainBackService.MyBinder
        handler()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

}