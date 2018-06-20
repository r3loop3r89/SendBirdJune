package com.shra1.sendbirdkt

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout
import com.shra1.sendbirdkt.fragments.HomeFragment
import com.shra1.sendbirdkt.utils.Utils.Companion.showToast

class MainActivity : AppCompatActivity() {

    lateinit var tbToolbar: Toolbar
    lateinit var mCtx: Context
    lateinit var flMAFragmentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCtx = this;

        tbToolbar = findViewById(R.id.tbToolbar)
        flMAFragmentContainer = findViewById(R.id.flMAFragmentContainer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 210)
        } else {
            MAIN()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 0..permissions.size - 1) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

            } else {
                showToast(this, "Please grant all permissions!")
                finish()
                return
            }
        }
        MAIN()
    }

    private fun MAIN() {
        setSupportActionBar(tbToolbar)

        changeFragment(HomeFragment.getInstance(), false);
    }

    public fun changeFragment(fragment: Fragment, addToBackstack: Boolean) {
        if (addToBackstack) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flMAFragmentContainer, fragment)
                    .addToBackStack("YES")
                    .commit()
        } else {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flMAFragmentContainer, fragment)
                    .commit()
        }
    }

    fun setToolbarTitle(title: String) {
        tbToolbar.setTitle(title)
    }

}
