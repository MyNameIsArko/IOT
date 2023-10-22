package com.fgieracki.iotapplication.data.local

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextCatcher {
    private var context: Context? = null

    fun getContext(): Context {
        return context!!
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun destroyContext(){
        this.context = null
    }
}