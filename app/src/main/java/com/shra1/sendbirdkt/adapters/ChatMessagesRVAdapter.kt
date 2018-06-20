package com.shra1.sendbirdkt.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.sendbird.android.UserMessage
import com.shra1.sendbirdkt.R
import com.shra1.sendbirdkt.SharedPreferenceStorage

class ChatMessagesRVAdapter(val mCtx: Context?, val messages: ArrayList<UserMessage>) : RecyclerView.Adapter<ChatMessagesRVAdapter.CMRVAViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CMRVAViewHolder {
        val v = LayoutInflater.from(mCtx).inflate(R.layout.chat_item_layout, parent, false);
        return CMRVAViewHolder(v)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: CMRVAViewHolder, position: Int) {

        val m = messages[position]
        if (m.sender.userId.equals(SharedPreferenceStorage.getInstance(mCtx!!).getUser())) {
            holder.outgoingMessage.visibility = VISIBLE
            holder.outgoingMessage.setText(m.message.toString())
            holder.incomingMessage.visibility = GONE
        } else {
            holder.incomingMessage.visibility = VISIBLE
            holder.outgoingMessage.visibility = GONE
            holder.incomingMessage.setText(m.message.toString())
        }

    }


    class CMRVAViewHolder(v: View?) : RecyclerView.ViewHolder(v) {
        lateinit var incomingMessage: TextView
        lateinit var outgoingMessage: TextView

        init {
            incomingMessage = v!!.findViewById(R.id.incomingMessage)
            outgoingMessage = v!!.findViewById(R.id.outgoingMessage)
        }
    }

    fun addMessage(m:UserMessage){
        messages.add(m)
        notifyDataSetChanged()
    }
}
