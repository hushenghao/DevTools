package com.dede.devtools.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dede.devtools.R
import kotlinx.android.synthetic.main.fragment_logcat.*

/**
 * Created by hsh on 2019-09-27 11:57
 */
class LogcatPageFragment : Fragment(), LogcatDataSource.Listener {

    private var ignoreUpdate = false
    private var init = true

    override fun onUpdate() {
        if (init) {
            rv_logcat.scrollToPosition(LogcatDataSource.getSize() - 1)
            init = false
        }
        if (ignoreUpdate) {
            return
        }
        rv_logcat.adapter?.notifyDataSetChanged()
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
        LogcatDataSource.addListener(this)
        rv_logcat.adapter = LogcatAdapter()
    }

    override fun onDestroyView() {
        LogcatDataSource.removeListener(this)
        super.onDestroyView()
    }

    private inner class LogcatAdapter : RecyclerView.Adapter<LogcatHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogcatHolder {
            val view =
                View.inflate(parent.context, R.layout.item_log_layout, null)
            return LogcatHolder(view)
        }

        override fun getItemCount(): Int {
            return LogcatDataSource.getSize()
        }

        var focusView: View? = null

        val focusChangeListener = View.OnFocusChangeListener { view, b ->
            if (!b) {
                if (view == focusView) {
                    ignoreUpdate = false
                }
                return@OnFocusChangeListener
            }
            focusView = view
            ignoreUpdate = true
        }

        override fun onBindViewHolder(holder: LogcatHolder, position: Int) {
            val log = LogcatDataSource.getData(position)
            holder.tvLog.text = log
            holder.tvLog.onFocusChangeListener = focusChangeListener
        }
    }

    private inner class LogcatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLog = view.findViewById<TextView>(R.id.tv_log)
    }

}