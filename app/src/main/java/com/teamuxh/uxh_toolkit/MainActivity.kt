package com.teamuxh.uxh_toolkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamuxh.uxh_toolkit.ui.main.MainFragment
import com.topjohnwu.superuser.Shell

class MainActivity : AppCompatActivity() {

    companion object{
        init {
            Shell.enableVerboseLogging = true
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment())
                .commitNow()
        }

    }
}