package com.dede.devtools.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dede.devtools.R
import com.dede.devtools.util.LogDecorator
import kotlinx.android.synthetic.main.fragment_logcat.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by hsh on 2019-09-27 11:57
 */
class LogcatPageFragment : Fragment() {

    private val threadPool by lazy {
        ThreadPoolExecutor(2, 2, 3, TimeUnit.MILLISECONDS, ArrayBlockingQueue(2))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logcat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_logcat.adapter = LogcatAdapter()
    }

    override fun onDestroy() {
        threadPool.shutdown()
        super.onDestroy()
    }

    private inner class LogcatAdapter : RecyclerView.Adapter<LogcatHolder>() {

        val logList = ArrayList<CharSequence>()
        val handler: Handler

        init {
            val pid = Process.myPid()
            val shell = "logcat | grep $pid"
            val process = Runtime.getRuntime().exec(shell)
            val out = BufferedReader(InputStreamReader(process.inputStream))
            threadPool.execute {
                process.waitFor()
            }
            threadPool.execute {
                var s = out.readLine()
                while (s != null) {
                    logList.add(LogDecorator.decorate(s))
                    s = out.readLine()
                }
                out.close()
            }

            handler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    notifyDataSetChanged()
                    if (msg.what == 1) {
                        rv_logcat.scrollToPosition(logList.size - 1)
                    }
                    sendEmptyMessageDelayed(0, 500)
                }
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            handler.sendEmptyMessageDelayed(1, 500)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            handler.removeCallbacksAndMessages(null)
            super.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogcatHolder {
            val view =
                View.inflate(parent.context, R.layout.item_log_layout, null)
            return LogcatHolder(view)
        }

        override fun getItemCount(): Int {
            return logList.size
        }

        override fun onBindViewHolder(holder: LogcatHolder, position: Int) {
            val log = logList[position]
            holder.tvLog.text = log
        }
    }

    private inner class LogcatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLog = view.findViewById<TextView>(R.id.tv_log)
    }

}