package com.teamuxh.uxh_toolkit.constant

class Native {
    companion object{
        init {
            System.loadLibrary("uxh_toolkit")
        }
    }
}