package com.example.my_bills

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.example.my_bills.MainActivity.Companion.UpLOAD


class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        try {
            if (isOnline(context)) {
                UpLOAD(true,context)

                Log.e("ismail", "Online Connect Intenet ")
            } else {
               UpLOAD(false,context)
                Log.e("ismail", "Conectivity Failure !!! ")
            }
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        }

    }
    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo

            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}