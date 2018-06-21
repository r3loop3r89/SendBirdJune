package com.shra1.sendbirdkt.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.sendbird.android.User
import com.shra1.sendbirdkt.MainActivity
import com.shra1.sendbirdkt.R
import com.shra1.sendbirdkt.SharedPreferenceStorage
import com.shra1.sendbirdkt.sendbird.SendBirdConnect
import com.shra1.sendbirdkt.sendbird.SendBirdConnectCallbacks
import com.shra1.sendbirdkt.utils.Constants.Companion.ADMIN
import com.shra1.sendbirdkt.utils.Utils

class HomeFragment : Fragment() {

    lateinit var bFHLoginAsAdmin: Button
    lateinit var bFHLoginAsUser: Button
    lateinit var etFHUserName: EditText
    lateinit var mCtx: Context
    lateinit var myActivity: MainActivity
    lateinit var pbFHProgressBar: ProgressBar

    companion object {
        var INSTANCE: HomeFragment? = null
        public fun getInstance(): HomeFragment {
            if (INSTANCE == null) {
                INSTANCE = HomeFragment()
            }
            return INSTANCE as HomeFragment;
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(v)

        myActivity = (activity as MainActivity)

        mCtx = container!!.context

        getUserFromSharedPref()

        bFHLoginAsUser.setOnClickListener {
            if (SharedPreferenceStorage.getInstance(mCtx).getUser().equals("User")) {
                var userName = etFHUserName.text.toString().trim()
                if (userName.length == 0) {
                    etFHUserName.setError("Please provide a username")
                    etFHUserName.requestFocus()
                } else {
                    SharedPreferenceStorage.getInstance(mCtx).setUser(userName)
                    Utils.showToast(activity, "Hello " + userName + "! Click again!")
                    getUserFromSharedPref()
                }
            } else {
                //let to open user fragment
                //connect to send bird and then only switch fragment
                SendBirdConnect(mCtx, SharedPreferenceStorage.getInstance(mCtx).getUser(), object : SendBirdConnectCallbacks {
                    override fun start() {
                        pbFHProgressBar.visibility = VISIBLE
                    }

                    override fun connectionSuccessfull(user: User?) {
                        myActivity.changeFragment(UserChatboxFragment.getInstance(), true)
                    }

                    override fun end() {
                        pbFHProgressBar.visibility = GONE
                    }
                })

            }
        }

        bFHLoginAsAdmin.setOnClickListener {
            SendBirdConnect(mCtx, ADMIN, object : SendBirdConnectCallbacks {

                override fun start() {
                    pbFHProgressBar.visibility = VISIBLE
                }

                override fun connectionSuccessfull(user: User?) {
                    myActivity.changeFragment(AdminUsersListFragment.getInstance(), true)
                }

                override fun end() {
                    pbFHProgressBar.visibility = GONE
                }

            })
        }

        return v;
    }

    private fun getUserFromSharedPref() {
        if (SharedPreferenceStorage.getInstance(mCtx).getUser().equals("User")) {

        } else {
            //let to open user fragment
            etFHUserName.setText(SharedPreferenceStorage.getInstance(mCtx).getUser())
            etFHUserName.isFocusable = false
            bFHLoginAsUser.setText("Enter")
        }
    }

    private fun initViews(v: View) {
        bFHLoginAsAdmin = v.findViewById(R.id.bFHLoginAsAdmin)
        bFHLoginAsUser = v.findViewById(R.id.bFHLoginAsUser)
        etFHUserName = v.findViewById(R.id.etFHUserName)
        pbFHProgressBar = v.findViewById(R.id.pbFHProgressBar)
    }

}
