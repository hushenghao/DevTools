package com.dede.devtools

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.dede.devtools.lifecycle.ActivityLifecycleCallbacksImpl
import com.dede.devtools.util.ShellUtils

/**
 * Created by hsh on 2019-09-26 19:21
 */
class DevToolsInitProvider : ContentProvider() {

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        val application = context as? Application ?: return false
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl.INSTANCE)
        ShellUtils.execCommand("logcat -c", false, false)
        return false
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return -1
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return -1
    }

    override fun getType(uri: Uri): String? {
        return null
    }

}