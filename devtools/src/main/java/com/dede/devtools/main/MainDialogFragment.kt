package com.dede.devtools.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.dede.devtools.R
import com.dede.devtools.page.PageFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_main_dialog.*

/**
 * Created by hsh on 2019-09-27 10:11
 */
class MainDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager.adapter = ToolsPageAdapter()
        tab_layout.setupWithViewPager(view_pager)
    }

    private inner class ToolsPageAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return PageFactory.getPage(position)
        }

        override fun getCount(): Int {
            return PageFactory.getPageCount()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return PageFactory.getPageTitle(position)
        }
    }

}