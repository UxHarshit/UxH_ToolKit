package com.teamuxh.uxh_toolkit.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.teamuxh.uxh_toolkit.ITestRoot
import com.teamuxh.uxh_toolkit.R
import com.teamuxh.uxh_toolkit.services.RootService
import com.teamuxh.uxh_toolkit.utils.Logger.Companion.logD
import com.teamuxh.uxh_toolkit.utils.Toast
import com.topjohnwu.superuser.Shell


class MainFragment : Fragment() {

    private var rootAccess: Boolean = false
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var libNameText: TextInputEditText
    private lateinit var processIdCLink: MaterialButton
    private lateinit var getLibAdrName: MaterialButton
    private lateinit var getLoadedLibs: MaterialButton
    private lateinit var clearOutput: MaterialButton
    private lateinit var textInput: TextView

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var viewModel: MainViewModel

    companion object {
        lateinit var iTestRoot: ITestRoot
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        autoCompleteTextView = view.findViewById(R.id.package_name)
        processIdCLink = view.findViewById(R.id.get_processID)
        getLibAdrName = view.findViewById(R.id.get_libAddress)
        clearOutput = view.findViewById(R.id.clearOutput)
        getLoadedLibs = view.findViewById(R.id.getLoadedLibs)

        libNameText = view.findViewById(R.id.lib_text)
        textInput = view.findViewById(R.id.inputTerminal)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        arrayAdapter = ArrayAdapter(requireActivity(), R.layout.list_item, getInstalledApps())
        autoCompleteTextView.setAdapter(arrayAdapter)


        getLoadedLibs.setOnClickListener {
            if (autoCompleteTextView.text.isEmpty())
                Toast.toastLong(requireActivity(), "package id can't be empty")
            else if (!rootAccess)
                Toast.toastLong(requireActivity(), "Root access not found")
            else {
                val resultPid = iTestRoot.getAppPid(autoCompleteTextView.text.toString())
                if (resultPid == -1)
                    Toast.toastLong(requireActivity(), "App is not running")
                else {
                    /* Thread {
                         val libAddress = iTestRoot.getLoadedLibrary(resultPid)
                         for (i in libAddress.indices){
                             requireActivity().runOnUiThread {
                                 updateTerminalOutput(libAddress[i])
                             }
                         }
                     }.start()*/
                    val libSheetDialog = LibSheetDialog(
                        iTestRoot.getLoadedLibrary(resultPid),
                        resultPid, ::updateTerminalOutput
                    )
                    libSheetDialog.show(requireActivity().supportFragmentManager, tag)
                }
            }
        }

        clearOutput.setOnClickListener { textInput.text = "" }

        getLibAdrName.setOnClickListener {
            if (autoCompleteTextView.text.isEmpty())
                Toast.toastLong(requireActivity(), "package id can't be empty")
            else if (libNameText.text!!.isEmpty())
                Toast.toastLong(requireActivity(), "lib name can't be empty")
            else if (!rootAccess)
                Toast.toastLong(requireActivity(), "Root access not found")
            else {
                val resultPid = iTestRoot.getAppPid(autoCompleteTextView.text.toString())
                if (resultPid == -1)
                    Toast.toastLong(requireActivity(), "App is not running")
                else {
                    val libAddress = iTestRoot.getLibAddress(resultPid, libNameText.text.toString())
                    updateTerminalOutput(
                        "($resultPid)[${libNameText.text}] Address is 0x${
                            java.lang.String.format(
                                "%02X",
                                libAddress
                            )
                        }"
                    )
                }
            }
        }
        processIdCLink.setOnClickListener {
            if (autoCompleteTextView.text.isEmpty())
                Toast.toastLong(requireActivity(), "package id can't be empty")
            else if (!rootAccess)
                Toast.toastLong(requireActivity(), "Root access not found")
            else {
                val resultPid = iTestRoot.getAppPid(autoCompleteTextView.text.toString())
                if (resultPid == -1)
                    Toast.toastLong(requireActivity(), "App is not running")
                else
                    updateTerminalOutput("App is running at pid $resultPid")
            }
        }

        Shell.getShell {
            rootAccess = true
            com.topjohnwu.superuser.ipc.RootService.bind(
                Intent(
                    requireActivity(),
                    RootService::class.java
                ), AidlConnection(true)
            )
        }
    }

    class AidlConnection(daemon: Boolean) : ServiceConnection {
        private var isDaemon: Boolean? = null

        init {
            isDaemon = daemon
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            iTestRoot = ITestRoot.Stub.asInterface(p1)
            logD("onServiceConnected: Connected")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            logD("onServiceDisconnected: Disconnected")
        }
    }

    private fun updateTerminalOutput(newMessage: String) {
        if (textInput.text.isEmpty()) {
            textInput.text = newMessage
        } else {
            textInput.text = getString(R.string.terminal, textInput.text.toString(), newMessage)
        }
    }

    private fun getInstalledApps(): List<String> {
        val packages: List<ApplicationInfo> =
            requireActivity().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val ret: MutableList<String> = ArrayList()
        for (s in packages) {
            ret.add(s.packageName)
        }
        return ret
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

}