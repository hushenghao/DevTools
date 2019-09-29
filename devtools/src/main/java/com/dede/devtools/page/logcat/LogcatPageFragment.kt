package com.dede.devtools.page.logcat

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

    private var init = true

    override fun onUpdate() {
        if (init) {
            rv_logcat.scrollToPosition(LogcatDataSource.getSize() - 1)
            init = false
        }
        if (adapter.hasFocus()) {
            return
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logcat, container, false)
    }

    private val adapter = LogcatAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogcatDataSource.addListener(this)
        rv_logcat.adapter = adapter

        bt_clear.setOnClickListener {
            adapter.clearFocus()
            LogcatDataSource.clear()
        }
    }

    override fun onDestroyView() {
        LogcatDataSource.removeListener(this)
        super.onDestroyView()
    }

    private inner class LogcatAdapter : RecyclerView.Adapter<LogcatHolder>(),
        View.OnFocusChangeListener {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogcatHolder {
            val view =
                View.inflate(parent.context, R.layout.item_log_layout, null)
            return LogcatHolder(view)
        }

        override fun getItemCount(): Int {
            return LogcatDataSource.getSize()
        }

        fun hasFocus(): Boolean {
            return focusViewSet.isNotEmpty()
        }

        fun clearFocus() {
            focusViewSet.clear()
        }

        private val focusViewSet = HashSet<View>()

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (v == null) return
            if (hasFocus) {
                focusViewSet.add(v)
            } else {
                focusViewSet.remove(v)
            }
        }

        override fun onBindViewHolder(holder: LogcatHolder, position: Int) {
            val log = LogcatDataSource.getData(position)
            holder.tvLog.text = log
            holder.itemView.onFocusChangeListener = this
        }
    }

    private inner class LogcatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLog = view.findViewById<TextView>(R.id.tv_log)
    }

}