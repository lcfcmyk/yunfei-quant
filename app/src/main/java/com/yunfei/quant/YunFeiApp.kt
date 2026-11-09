package com.yunfei.quant

import android.app.Application
import com.yunfei.quant.util.ContextHolder

class YunFeiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(this)
    }
}
