package com.shra1.sendbirdkt

import android.app.Application
import com.sendbird.android.SendBird
import com.shra1.sendbirdkt.utils.Constants

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        SendBird.init(Constants.APP_ID, this)
    }
}