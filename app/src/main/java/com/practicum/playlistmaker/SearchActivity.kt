package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SearchActivity : AppCompatActivity() {

    companion object {
        const val INPUT_EDIT_TEXT_VALUE = "INPUT_EDIT_TEXT_VALUE"
        const val INPUT_EDIT_TEXT_DEF = ""
    }
    private var inputEditTextValue = INPUT_EDIT_TEXT_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val tbSearch = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.tb_search)
        tbSearch.setNavigationOnClickListener {
            finish()
        }

        val imgClear = findViewById<ImageView>(R.id.clearIcon)
        imgClear.setOnClickListener {
            val inputEditText = findViewById<EditText>(R.id.inputEditText)
            inputEditText.setText(INPUT_EDIT_TEXT_DEF)
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    inputEditTextValue = INPUT_EDIT_TEXT_DEF
                    imgClear.setVisibility(View.GONE)
                    val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                } else {
                    inputEditTextValue = s.toString()
                    imgClear.setVisibility(View.VISIBLE)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(savedInstanceState.getString(INPUT_EDIT_TEXT_VALUE, INPUT_EDIT_TEXT_DEF))
        if (inputEditText.text.isNotEmpty()) {
            inputEditText.setSelection(inputEditText.text.length)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(INPUT_EDIT_TEXT_VALUE, inputEditTextValue)
    }
}