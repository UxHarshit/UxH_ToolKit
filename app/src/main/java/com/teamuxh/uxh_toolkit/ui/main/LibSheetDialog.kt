package com.teamuxh.uxh_toolkit.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamuxh.uxh_toolkit.R
import com.teamuxh.uxh_toolkit.utils.Logger.Companion.logD
import java.util.*
import kotlin.collections.ArrayList

class LibSheetDialog(
    private var array: Array<String>,
    private var resultPid: Int,
    private val terminal: (string: String) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var arrayBack: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        arrayBack = array
        val view = inflater.inflate(R.layout.lib_dialog_sheet, container, false)
        val showResult = view.findViewById<TextView>(R.id.show_result)

        val recyclerView = view.findViewById<RecyclerView>(R.id.libRecycleView)
        val searchView = view.findViewById<SearchView>(R.id.search_lib)
        val dialogAdapter = DialogAdapter()

        searchView.setOnCloseListener {
            dialogAdapter.filterList(array)
            return@setOnCloseListener false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList: ArrayList<String> = ArrayList()
                for (i in arrayBack.indices) {
                    if (arrayBack[i].lowercase().contains(newText.lowercase())) {
                        filteredList.add(arrayBack[i])
                    }
                }
                dialogAdapter.filterList(filteredList.toTypedArray())

                return false
            }
        })
        showResult.text = getString(R.string.showing_results_302, array.size)
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = dialogAdapter
        dialogAdapter.notifyDataSetChanged()
        return view
    }

    inner class DialogAdapter :
        RecyclerView.Adapter<DialogAdapter.Adapter>() {

        inner class Adapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView

            init {
                textView = itemView.findViewById(R.id.libName)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.text_row, parent, false)
            return Adapter(view)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun filterList(filterList: Array<String>) {
            array = filterList
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: Adapter, position: Int) {
            holder.textView.text = array[position]
            holder.textView.setOnClickListener {
                val libAddress =
                    MainFragment.iTestRoot.getLibAddress(resultPid, holder.textView.text.toString())
                terminal(
                    "($resultPid)[${holder.textView.text}] Address is 0x${
                        java.lang.String.format(
                            "%02X",
                            libAddress
                        )
                    }"
                )
                dismiss()


            }
        }

        override fun getItemCount(): Int = array.size
    }
}


