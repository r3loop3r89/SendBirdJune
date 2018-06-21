package com.shra1.sendbirdkt.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
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
import com.shra1.sendbirdkt.SharedPreferenceStorage
import com.shra1.sendbirdkt.adapters.ChatMessagesRVAdapter
import com.shra1.sendbirdkt.utils.Constants.Companion.ADMIN
import com.shra1.sendbirdkt.utils.Utils.Companion.showToast

class UserChatboxFragment : Fragment() {

    lateinit var rvFUCChatMessages: RecyclerView
    lateinit var etFUCMessage: EditText
    lateinit var bFUCSend: Button
    lateinit var myActivity: MainActivity
    lateinit var mCtx: Context
    lateinit var pbFUCProgressBar: ProgressBar
    var myGroupChannel: GroupChannel? = null
    var previousMessageListQuery: PreviousMessageListQuery? = null
    lateinit var messages: ArrayList<UserMessage>
    lateinit var chatMessagesRVAdapter: ChatMessagesRVAdapter


    companion object {
        var INSTANCE: UserChatboxFragment? = null
        public fun getInstance(): UserChatboxFragment {
            if (INSTANCE == null) {
                INSTANCE = UserChatboxFragment()
            }
            return INSTANCE as UserChatboxFragment;
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_user_chatbox, container, false)
        mCtx = container!!.context

        initViews(v)

        etFUCMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Handler().postDelayed({
                    myGroupChannel?.endTyping()
                }, 1000)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if((s?.equals(" "))==false) {
                    myGroupChannel?.startTyping()
                }
            }

        })

        myActivity = (activity as MainActivity)

        myActivity.setToolbarTitle(SharedPreferenceStorage.getInstance(mCtx).getUser() + " chatting with Admin")

        //STEPS
        //(1) CREATE GROUP CHANNEL
        pbFUCProgressBar.visibility = VISIBLE
        GroupChannel.createChannelWithUserIds(arrayListOf(ADMIN),
                true,
                SharedPreferenceStorage.getInstance(mCtx).getUser(), null, null,
                object : GroupChannel.GroupChannelCreateHandler {
                    override fun onResult(channel: GroupChannel?, e: SendBirdException?) {
                        if (e != null) {
                            e.printStackTrace()
                            return
                        }
                        if (myGroupChannel == null) {
                            myGroupChannel = channel
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
                                    rvFUCChatMessages.layoutManager = LinearLayoutManager(mCtx)
                                    chatMessagesRVAdapter = ChatMessagesRVAdapter(mCtx, messages)
                                    rvFUCChatMessages.adapter = chatMessagesRVAdapter
                                    pbFUCProgressBar.visibility = GONE
                                    etFUCMessage.isEnabled=true
                                }
                            })
                        }

                        //(3) SETUP CHANNEL HANDLER
                        SendBird.addChannelHandler(
                                SharedPreferenceStorage.getInstance(mCtx).getUser(),
                                object : SendBird.ChannelHandler() {
                                    override fun onMessageReceived(bc: BaseChannel?, bm: BaseMessage?) {
                                        if (bm is UserMessage) {
                                            chatMessagesRVAdapter.addMessage(bm)
                                        }
                                    }

                                }
                        )

                    }
                })

        bFUCSend.setOnClickListener {
            var message = etFUCMessage.text.toString().trim()
            pbFUCProgressBar.visibility= VISIBLE
            myGroupChannel!!.sendUserMessage(message, object : BaseChannel.SendUserMessageHandler {
                override fun onSent(um: UserMessage?, e: SendBirdException?) {
                    pbFUCProgressBar.visibility= GONE
                    if (e != null) {
                        e.printStackTrace()
                        return
                    }
                    chatMessagesRVAdapter.addMessage(um!!)
                    //Utils.showToast(myActivity, "Sent")
                    etFUCMessage.setText("")
                }
            })
        }



        return v
    }

    private fun initViews(v: View) {
        rvFUCChatMessages = v.findViewById(R.id.rvFUCChatMessages)
        etFUCMessage = v.findViewById(R.id.etFUCMessage)
        bFUCSend = v.findViewById(R.id.bFUCSend)
        pbFUCProgressBar = v.findViewById(R.id.pbFUCProgressBar)
    }

    override fun onDestroy() {
        myActivity.setToolbarTitle(mCtx.resources.getString(R.string.app_name))
        SendBird.disconnect {
            showToast(myActivity, "Sendbird disconnected")
        }
        INSTANCE = null
        super.onDestroy()
    }

}
