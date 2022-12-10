package com.teamuxh.uxh_toolkit.utils

import android.content.Context

class Toast {
    companion object{
        /**
         * Toast for ease
         */
        fun toastLong(context: Context,message : String){android.widget.Toast.makeText(context,message,android.widget.Toast.LENGTH_SHORT).show()}
        fun toastShort(context: Context,message : String){android.widget.Toast.makeText(context,message,android.widget.Toast.LENGTH_SHORT).show()}
    }
}