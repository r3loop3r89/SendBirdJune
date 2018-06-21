package com.shra1.sendbirdkt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sendbird.android.*
import com.shra1.sendbirdkt.R

class MyGroupChannelAdapter(private val mCtx: Context, private val chatUserList: ArrayList<GroupChannel>, private val c:MyGroupChannelAdapterCallbacks) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var v = convertView
        var h: MGCAViewHolder
        if (v == null) {
            v = LayoutInflater.from(mCtx).inflate(R.layout.user_list_item, parent, false)
            h = MGCAViewHolder(v)
            v.tag = h
        } else {
            h = v.tag as MGCAViewHolder
        }

        var gc = getItem(position)

        SendBird.addChannelHandler(gc.name, object : SendBird.ChannelHandler() {
            override fun onMessageReceived(p0: BaseChannel?, p1: BaseMessage?) {
            }

            override fun onTypingStatusUpdated(channel: GroupChannel?) {
                if (gc.name.equals(channel!!.name)) {
                    if (channel!!.isTyping) {
                        h.tvULIUserTyping.visibility = VISIBLE
                    } else {
                        h.tvULIUserTyping.visibility = INVISIBLE
                    }
                }
            }

            override fun onUserEntered(channel: OpenChannel?, user: User?) {
                super.onUserEntered(channel, user)
                h.ivULIOnlineStatus.setImageDrawable(mCtx.resources.getDrawable(R.drawable.shape_online))
            }

            override fun onUserJoined(channel: GroupChannel?, user: User?) {
                super.onUserJoined(channel, user)
                h.ivULIOnlineStatus.setImageDrawable(mCtx.resources.getDrawable(R.drawable.shape_online))
            }

            override fun onUserExited(channel: OpenChannel?, user: User?) {
                super.onUserExited(channel, user)
                h.ivULIOnlineStatus.setImageDrawable(mCtx.resources.getDrawable(R.drawable.shape_offline))
            }

            override fun onUserLeft(channel: GroupChannel?, user: User?) {
                super.onUserLeft(channel, user)
                h.ivULIOnlineStatus.setImageDrawable(mCtx.resources.getDrawable(R.drawable.shape_offline))
            }
        })

        h.tvULIUserName.setText(gc.name)

        v!!.setOnClickListener {
            c.onItemClick(gc)
        }

        return v
    }

    override fun getItem(position: Int): GroupChannel {
        return chatUserList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return chatUserList.size
    }


    class MGCAViewHolder {
        lateinit var tvULIUserName: TextView
        lateinit var tvULIUserTyping: TextView
        lateinit var ivULIOnlineStatus: ImageView

        constructor(v: View) {
            tvULIUserName = v.findViewById(R.id.tvULIUserName)
            tvULIUserTyping = v.findViewById(R.id.tvULIUserTyping)
            ivULIOnlineStatus = v.findViewById(R.id.ivULIOnlineStatus)
        }
    }

    interface MyGroupChannelAdapterCallbacks{
        fun onItemClick(gc:GroupChannel)
    }
}
