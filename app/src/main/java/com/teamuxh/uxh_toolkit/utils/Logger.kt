package com.teamuxh.uxh_toolkit.utils

import android.util.Log

class Logger {
    companion object{
        /**
         * Simple Logger to debug your applications
         */
        private const val tag = "TeamUxHLog"
        private const val debug = true
        fun logD(message : String){if (debug) Log.d(tag,message)}
        fun logV(message: String){if (debug) Log.v(tag,message)}
        fun logI(message: String){if (debug) Log.i(tag,message)}
        fun logE(message: String){if (debug) Log.e(tag,message)}
    }
}