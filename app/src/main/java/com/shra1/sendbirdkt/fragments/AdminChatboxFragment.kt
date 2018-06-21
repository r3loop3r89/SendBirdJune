package com.shra1.sendbirdkt.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.sendbird.android.*
import com.shra1.sendbirdkt.MainActivity
import com.shra1.sendbirdkt.R
import com.shra1.sendbirdkt.adapters.AdminChatMessagesRVAdapter

class AdminChatboxFragment : Fragment() {

    private lateinit var selectedUser: String

    lateinit var rvFACChatMessages: RecyclerView
    lateinit var pbFACProgressBar: ProgressBar
    lateinit var etFACMessage: EditText
    lateinit var bFACSend: Button

    lateinit var myActivity: MainActivity

    var myGroupChannel: GroupChannel? = null
    var previousMessageListQuery: PreviousMessageListQuery? = null
    lateinit var messages: ArrayList<UserMessage>
    lateinit var chatMessagesRVAdapter: AdminChatMessagesRVAdapter

    lateinit var mCtx: Context


    companion object {
        var INSTANCE: AdminChatboxFragment? = null
        public fun getInstance(sUser: String): AdminChatboxFragment {
            if (INSTANCE == null) {
                INSTANCE = AdminChatboxFragment()
                INSTANCE!!.selectedUser = sUser
            }
            return INSTANCE as AdminChatboxFragment;
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_admin_chatbox, container, false)
        mCtx = container!!.context

        initViews(v)

        myActivity = (activity as MainActivity)


        //STEPS
        //(1) CREATE GROUP CHANNEL
        pbFACProgressBar.visibility = VISIBLE
        GroupChannel.createChannelWithUserIds(arrayListOf(selectedUser),
                true,
                object : GroupChannel.GroupChannelCreateHandler {
                    override fun onResult(gc: GroupChannel?, e: SendBirdException?) {
                        if (e != null) {
                            e.printStackTrace()
                            return
                        }
                        if (myGroupChannel == null) {
                            myGroupChannel = gc
                        }

                        //(2) LOAD HISTORY
                        if (previousMessageListQuery == null) {
                            previousMessageListQuery = myGroupChannel!!.createPreviousMessageListQuery()

                            previousMessageListQuery!!.load(30, false, object : PreviousMessageListQuery.MessageListQueryResult {
                                override fun onResult(messageList: MutableList<BaseMessage>?, e: SendBirdException?) {
                                    if (e != null) {
                                        e.printStackTrace()
                                        return
                                    }

                                    messages = ArrayList()
                                    for (b in messageList!!) {
                                        if (b is UserMessage) {
                                            messages.add(b)
                                        }
                                    }
                                    rvFACChatMessages.layoutManager = LinearLayoutManager(mCtx)
                                    chatMessagesRVAdapter = AdminChatMessagesRVAdapter(mCtx, messages)
                                    rvFACChatMessages.adapter = chatMessagesRVAdapter
                                    pbFACProgressBar.visibility = View.GONE
                                    etFACMessage.isEnabled = true
                                }
                            })
                        }
                    }
                })

        bFACSend.setOnClickListener {
            var message = etFACMessage.text.toString().trim()
            pbFACProgressBar.visibility = VISIBLE
            myGroupChannel!!.sendUserMessage(message, object : BaseChannel.SendUserMessageHandler {
                override fun onSent(um: UserMessage?, e: SendBirdException?) {
                    pbFACProgressBar.visibility = GONE
                    if (e != null) {
                        e.printStackTrace()
                        return
                    }
                    chatMessagesRVAdapter.addMessage(um!!)
                    //Utils.showToast(myActivity, "Sent")
                    etFACMessage.setText("")
                }
            })
        }

        return v
    }

    private fun initViews(v: View) {
        rvFACChatMessages = v.findViewById(R.id.rvFACChatMessages)
        pbFACProgressBar = v.findViewById(R.id.pbFACProgressBar)
        etFACMessage = v.findViewById(R.id.etFACMessage)
        bFACSend = v.findViewById(R.id.bFACSend)
    }

    override fun onDestroy() {
        //myActivity.setToolbarTitle(mCtx.resources.getString(R.string.app_name))
        INSTANCE = null
        super.onDestroy()
    }
}
