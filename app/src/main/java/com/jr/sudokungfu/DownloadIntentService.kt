package com.jr.sudokungfu

import android.app.IntentService
import android.content.Intent
import android.util.Log

class DownloadIntentService : IntentService("DownloadIntentService") {

    companion object {
        val TAG: String = DownloadIntentService::class.java.simpleName
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        // This method is called on a separate worker thread
        // Perform your background tasks here
        val inputText = intent?.getStringExtra("input_text")
        Log.d(TAG, "Received Input: $inputText")

        for (i in 1..10) {
            Log.d(TAG, "Working : $i")
            try {
                Thread.sleep(1000L)
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }

        Log.d(TAG, "Task Completed")
    }
}