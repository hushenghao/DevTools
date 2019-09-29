package com.dede.devtools.page

import androidx.fragment.app.Fragment
import com.dede.devtools.page.logcat.LogcatPageFragment
import com.dede.devtools.page.tools.ToolsPageFragment

/**
 * Created by hsh on 2019-09-27 13:47
 */
object PageFactory {

    private val pages = listOf(
        LogcatPageFragment::class.java,
        ToolsPageFragment::class.java
    )

    private val titles = listOf(
        "logcat",
        "tools"
    )

    fun getPageCount(): Int {
        return pages.size
    }

    fun getPage(position: Int): Fragment {
        val clazz = pages[position]
        return clazz.newInstance()
    }

    fun getPageTitle(position: Int): String {
        return titles[position]
    }
}