package com.fgieracki.iotapplication.data.local

import android.annotation.SuppressLint
import android.app.Activity

@SuppressLint("StaticFieldLeak")
object ActivityCatcher {
    private var activity: Activity? = null

    fun getActivity(): Activity {
        return activity!!
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun destroyActivity(){
        this.activity = null
    }
}