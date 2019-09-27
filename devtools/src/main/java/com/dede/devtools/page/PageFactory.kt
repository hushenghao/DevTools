package com.dede.devtools.page

import androidx.fragment.app.Fragment

/**
 * Created by hsh on 2019-09-27 13:47
 */
object PageFactory {

    fun getPageCount(): Int {
        return 2
    }

    fun getPage(position: Int): Fragment {
        return when (position) {
            0 -> LogcatPageFragment()
            1 -> ToolsPageFragment()
            else -> LogcatPageFragment()
        }
    }

    fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "logcat"
            1 -> "tools"
            else -> "logcat"
        }
    }
}