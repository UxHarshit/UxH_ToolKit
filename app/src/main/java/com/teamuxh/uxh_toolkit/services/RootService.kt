package com.teamuxh.uxh_toolkit.services

import android.content.Intent
import com.teamuxh.uxh_toolkit.ITestRoot
import com.teamuxh.uxh_toolkit.constant.NativeRoot
import com.teamuxh.uxh_toolkit.utils.Logger.Companion.logD


class RootService : com.topjohnwu.superuser.ipc.RootService(){

    class IRootClass : ITestRoot.Stub() {
        override fun getUid(): Int {
            return NativeRoot.nativeGetUid()
        }

        override fun getAppPid(packageName: String?): Int {
            return NativeRoot.nativeGetPid(packageName.toString())
        }

        override fun getLibAddress(pid: Int, libName: String?): Int {
            return NativeRoot.getModuleAddress(pid,libName.toString())
        }
        override fun getLoadedLibrary(pid : Int): Array<out String> {
            return NativeRoot.getLoadedLibraries(pid)
        }


    }
    override fun onBind(intent: Intent): IRootClass {
        logD("onBind: Called")
        return IRootClass()
    }


}