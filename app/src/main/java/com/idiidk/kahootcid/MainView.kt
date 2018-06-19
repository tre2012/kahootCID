package com.idiidk.kahootcid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.idiidk.kahootcid.kahoot.Kahoot

class MainView : AppCompatActivity() {
    private lateinit var kahootSession: Kahoot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        kahootSession = Kahoot(sharedPref.getString("gameName", "kahoot-tools"), sharedPref.getInt("gamePin", 0))

        kahootSession.connect({ session ->
            if (session != null) {

            } else {
                //Error
            }
        })
    }
}