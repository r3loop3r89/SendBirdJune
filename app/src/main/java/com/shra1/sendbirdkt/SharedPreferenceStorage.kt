package com.shra1.sendbirdkt

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceStorage {
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    constructor(context: Context) {
        sharedPreference = context.getSharedPreferences(
                context.packageName + "." + javaClass.name,
                Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
    }

    companion object {
        private var INSTANCE: SharedPreferenceStorage? = null
        fun getInstance(context: Context): SharedPreferenceStorage {
            if (INSTANCE == null) {
                INSTANCE = SharedPreferenceStorage(context)
            }
            return INSTANCE as SharedPreferenceStorage
        }
    }

    fun setUser(User:String){
        editor.putString("User", User);
        editor.commit()
    }

    fun getUser():String{
        return sharedPreference.getString("User", "User")
    }
}