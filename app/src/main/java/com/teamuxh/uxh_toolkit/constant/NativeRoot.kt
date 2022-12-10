package com.teamuxh.uxh_toolkit.constant

import android.os.Process

class NativeRoot {
    companion object{
        external fun nativeGetUid() : Int
        external fun nativeGetPid(packageName : String) : Int
        external fun getModuleAddress(pid : Int,libName : String) : Int
        external fun getLoadedLibraries(pid : Int) :  Array<out String>
        init {
            if (Process.myUid() == 0)
                System.loadLibrary("root")
        }
    }
}