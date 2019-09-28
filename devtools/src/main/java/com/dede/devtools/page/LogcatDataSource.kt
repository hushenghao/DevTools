package com.dede.devtools.page

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Process
import com.dede.devtools.util.LogDecorator
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object LogcatDataSource : Handler(Looper.getMainLooper()) {

    interface Listener {
        fun onUpdate()
    }

    private val listeners: ArrayList<Listener> = ArrayList()

    @Synchronized
    fun addListener(listener: Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    @Synchronized
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    @Synchronized
    private fun callbackUpdate() {
        for (listener in listeners) {
            listener.onUpdate()
        }
    }

    private val threadPool by lazy {
        ThreadPoolExecutor(
            2, 4, 10, TimeUnit.SECONDS,
            LinkedBlockingDeque()
        )
    }

    private const val MSG_UPDATE = 1

    private val logList = ArrayList<CharSequence>()

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_UPDATE -> {
                callbackUpdate()
            }
        }
    }

    private val process: java.lang.Process

    init {
        val pid = Process.myPid()
        val shell = "logcat | grep $pid"
        process = Runtime.getRuntime().exec(shell)
        val out = BufferedReader(InputStreamReader(process.inputStream))
        threadPool.execute {
            process.waitFor()
        }

        threadPool.execute {
            var s = out.readLine()
            while (s != null) {
                logList.add(LogDecorator.decorate(s))

                sendMessageDelayed(
                    Message.obtain(this, MSG_UPDATE, logList.size),
                    100
                )

                try {
                    s = out.readLine()
                } catch (ignore: IOException) {
                    break
                }
                // 阻塞时间在100毫秒内，移除消息，忽略刷新
                removeMessages(MSG_UPDATE)
            }
            try {
                out.close()
            } catch (ignore: IOException) {
            }
        }
    }

    fun getData(position: Int): CharSequence {
        return logList[position]
    }

    fun getSize(): Int {
        return logList.size
    }

    fun release() {
        process.destroy()
        removeCallbacksAndMessages(null)
    }

}