package com.dede.devtools.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.dede.devtools.main.MainPop

/**
 * Created by hsh on 2019-09-26 19:30
 */
internal class ActivityLifecycleCallbacksImpl private constructor() :
    Application.ActivityLifecycleCallbacks {

    companion object {
        val INSTANCE by lazy { ActivityLifecycleCallbacksImpl() }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        MainPop.show(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        MainPop.hide(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}