package com.shra1.sendbirdkt.sendbird

import android.content.Context
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.shra1.sendbirdkt.SharedPreferenceStorage

class SendBirdConnect {

    constructor(context: Context,connectedAs:String, c: SendBirdConnectCallbacks) {
        connect(context,connectedAs, c)
    }

    fun connect(context: Context,connectedAs:String, c: SendBirdConnectCallbacks) {
        c.start()
        SendBird.connect(
                connectedAs,
                object : SendBird.ConnectHandler {
                    override fun onConnected(user: User?, e: SendBirdException?) {
                        c.end()
                        if (e != null) {
                            return
                        }
                        c.connectionSuccessfull(user)
                    }
                }
        )
    }


}

public interface SendBirdConnectCallbacks {
    public fun connectionSuccessfull(user: User?)
    fun start()
    fun end()
}