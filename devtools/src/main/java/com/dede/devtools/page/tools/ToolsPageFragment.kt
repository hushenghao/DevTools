package com.dede.devtools.page.tools

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.dede.devtools.R

/**
 * Created by hsh on 2019-09-27 13:59
 */
class ToolsPageFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_tools_page)
    }
}