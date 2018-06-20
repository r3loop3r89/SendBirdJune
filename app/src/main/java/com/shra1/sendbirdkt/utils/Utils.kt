package com.shra1.sendbirdkt.utils

import android.app.Activity
import android.widget.Toast

class Utils {
companion object {
    fun showToast(activity: Activity?, text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

}
}
