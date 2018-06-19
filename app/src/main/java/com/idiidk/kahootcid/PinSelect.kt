package com.idiidk.kahootcid

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Button
import android.widget.EditText
import com.idiidk.kahootcid.kahoot.checkPin
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout

class PinSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_select)

        val testSessionButton = findViewById<Button>(R.id.testSessionButton)
        val pinBox = findViewById<EditText>(R.id.pinEditText)
        val nameBox = findViewById<EditText>(R.id.nameEditText)
        testSessionButton.setOnClickListener {
            checkPin(pinBox.text.toString().toInt(), { exists ->
                if (exists) {
                    val sharedPref = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("gamePin", pinBox.text.toString().toInt())
                        putString("gameName", nameBox.text.toString())
                        commit()
                    }
                    var mainView = Intent(this, MainView::class.java)
                    startActivity(mainView)
                } else {
                    var errorSnackbar = Snackbar.make(findViewById(R.id.pinSelectRoot),
                            R.string.game_not_found, Snackbar.LENGTH_LONG)
                    errorSnackbar.show()

                    var view = findViewById<LinearLayout>(R.id.pinSelectRoot)
                    var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            })
        }
    }
}
