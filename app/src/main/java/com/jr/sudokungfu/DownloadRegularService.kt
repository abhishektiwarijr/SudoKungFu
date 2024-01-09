package com.jr.sudokungfu

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DownloadRegularService : Service() {

    companion object {
        val TAG: String = DownloadRegularService::class.java.simpleName
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val inputText = intent?.getStringExtra("input_string")
        Log.d(TAG, "Received Input: $inputText")
        Log.d(TAG, "My DownloadRegularService started")

        Thread {
            for (i in 1..10) {
                Log.d(TAG, "Working : $i")
                try {
                    Thread.sleep(1000L)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
            Log.d(TAG, "Task Completed")
            stopSelf() // Stop the service when the task is complete
        }.start()

        return START_STICKY // Restart the service if it's terminated by the system
    }

}