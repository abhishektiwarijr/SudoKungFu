package com.jr.sudokungfu

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.widget.Toast

const val MESSAGE_HI = 1

class MyBoundService : Service() {
    private val localBinder = LocalBinder()
    private lateinit var messenger: Messenger

    override fun onBind(intent: Intent?): IBinder {
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
//        return localBinder
    }

    fun callMeFromActivity() {
        Log.e("MyBoundService", "MyBoundService.........................")
        Toast.makeText(this, "Hi..!", Toast.LENGTH_SHORT).show()
    }

    inner class LocalBinder : Binder() {
        fun getMyBoundService() = this@MyBoundService
    }

    internal class IncomingHandler(
        context: Context,
        private val applicationContext : Context = context.applicationContext
    ) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_HI -> {
                    Toast.makeText(applicationContext, "Hey there..!", Toast.LENGTH_SHORT).show()
                }

                else -> super.handleMessage(msg)
            }
        }
    }
}