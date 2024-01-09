package com.jr.sudokungfu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.jr.sudokungfu.ui.game.SudokuBoard
import com.jr.sudokungfu.ui.theme.SudoKungFuTheme

class MainActivity : ComponentActivity() {
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SudoKungFuTheme {
                Surface(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SudokuBoard()
                }
            }
        }
//        startMyDownloadIntentService()
//        startMyDownloadRegularService()
//        startMyDownloadForegroundService()
//        startMyBoundService()
    }

    private fun startMyDownloadIntentService() {
        val intent = Intent(this, DownloadIntentService::class.java)
        intent.putExtra("input_string", "Download from MainActivity")
        startService(intent)
    }

    private fun startMyDownloadRegularService() {
        val intent = Intent(this, DownloadRegularService::class.java)
        intent.putExtra("input_string", "Download from MainActivity")
        startService(intent)
    }

    private fun startMyDownloadForegroundService() {
        val intent = Intent(this, DownloadForegroundService::class.java)
        intent.putExtra("input_string", "Download from MainActivity")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun startMyBoundService() {
        Intent(this, MyBoundService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
//        val intent = Intent(this, MyBoundService::class.java)
//        startService(intent)
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //#1
            // Service is connected, you can call the public method here
//            val myBoundService = (service as MyBoundService.LocalBinder).getMyBoundService()
//            myBoundService.callMeFromActivity()

            //#2
            val myBoundService = Messenger(service)
            val msg: Message = Message.obtain(null, MESSAGE_HI, 0, 0)
            try {
                myBoundService.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            mBound = true

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        mBound = false
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SudoKungFuTheme {
        SudokuBoard()
    }
}