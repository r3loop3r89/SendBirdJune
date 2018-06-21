package com.shra1.sendbirdkt.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sendbird.android.UserMessage
import com.shra1.sendbirdkt.R
import com.shra1.sendbirdkt.utils.Constants.Companion.ADMIN

class AdminChatMessagesRVAdapter(val mCtx: Context?, val messages: ArrayList<UserMessage>) : RecyclerView.Adapter<AdminChatMessagesRVAdapter.ACMRVAViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ACMRVAViewHolder {
        val v = LayoutInflater.from(mCtx).inflate(R.layout.chat_item_layout, parent, false);
        return ACMRVAViewHolder(v)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ACMRVAViewHolder, position: Int) {

        val m = messages[position]
        if (m.sender.userId.equals(ADMIN)) {
            holder.outgoingMessage.visibility = View.VISIBLE
            holder.outgoingMessage.setText(m.message.toString())
            holder.incomingMessage.visibility = View.GONE
        } else {
            holder.incomingMessage.visibility = View.VISIBLE
            holder.outgoingMessage.visibility = View.GONE
            holder.incomingMessage.setText(m.message.toString())
        }

    }

    fun addMessage(m: UserMessage) {
        messages.add(m)
        notifyDataSetChanged()
    }


    class ACMRVAViewHolder(v: View?) : RecyclerView.ViewHolder(v) {
        lateinit var incomingMessage: TextView
        lateinit var outgoingMessage: TextView

        init {
            incomingMessage = v!!.findViewById(R.id.incomingMessage)
            outgoingMessage = v!!.findViewById(R.id.outgoingMessage)
        }
    }
}
