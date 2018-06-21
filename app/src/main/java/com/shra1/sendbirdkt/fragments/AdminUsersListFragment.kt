package com.shra1.sendbirdkt.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelListQuery
import com.sendbird.android.SendBird
import com.shra1.sendbirdkt.MainActivity
import com.shra1.sendbirdkt.R
import com.shra1.sendbirdkt.SharedPreferenceStorage
import com.shra1.sendbirdkt.adapters.MyGroupChannelAdapter
import com.shra1.sendbirdkt.utils.Constants.Companion.ADMIN
import com.shra1.sendbirdkt.utils.Utils

class AdminUsersListFragment : Fragment() {

    lateinit var myActivity: MainActivity
    lateinit var mCtx: Context
    lateinit var lvFAULUsersList: ListView
    lateinit var chatUserList: ArrayList<GroupChannel>

    companion object {
        var INSTANCE: AdminUsersListFragment? = null
        public fun getInstance(): AdminUsersListFragment {
            if (INSTANCE == null) {
                INSTANCE = AdminUsersListFragment()
            }
            return INSTANCE as AdminUsersListFragment;
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_admin_user_list, container, false)
        mCtx = container!!.context
        myActivity = (mCtx as MainActivity)
        myActivity.setToolbarTitle("Users to chat with")

        initViews(v)

        chatUserList = ArrayList()

        val channelListQuery = GroupChannel.createMyGroupChannelListQuery()
        channelListQuery.setIncludeEmpty(true)
        channelListQuery.next(GroupChannelListQuery.GroupChannelListQueryResultHandler { list, e ->
            if (e != null) {
                // Error.
                return@GroupChannelListQueryResultHandler
            }

            for (g in list) {
                chatUserList.add(g)
            }

            val adapter = MyGroupChannelAdapter(
                    mCtx,
                    chatUserList,
                    object : MyGroupChannelAdapter.MyGroupChannelAdapterCallbacks {
                        override fun onItemClick(gc: GroupChannel) {
                            Utils.showToast(myActivity, "Clicked on " + gc.name)
                            myActivity.changeFragment(AdminChatboxFragment.getInstance(gc.name), true)
                        }
                    })

            lvFAULUsersList.adapter = adapter

        })

        return v
    }

    private fun initViews(v: View) {
        lvFAULUsersList = v.findViewById(R.id.lvFAULUsersList)
    }

    override fun onDestroy() {
        myActivity.setToolbarTitle(mCtx.resources.getString(R.string.app_name))
        SendBird.disconnect {
            Utils.showToast(myActivity, "Sendbird disconnected")
        }
        INSTANCE = null
        super.onDestroy()
    }
}
