package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tbSettings = findViewById<MaterialToolbar>(R.id.tb_settings)
        tbSettings.setNavigationOnClickListener {
            finish()
        }

        val appClass = (applicationContext as App)
        val swtDarkTheme = findViewById<SwitchMaterial>(R.id.swt_dark_theme)
        swtDarkTheme.isChecked = appClass.getDarkTheme()
        swtDarkTheme.setOnCheckedChangeListener { switcher, checked ->
            appClass.switchTheme(checked)
        }

        val btnShareApp = findViewById<MaterialTextView>(R.id.btn_share_app)
        btnShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.intent_share_app))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.intent_share_app_header)))
        }

        val btnSupport = findViewById<MaterialTextView>(R.id.btn_support)
        btnSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = getString(R.string.mailto_intent_data).toUri()
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            startActivity(supportIntent)
        }

        val btnUserAgreement = findViewById<MaterialTextView>(R.id.btn_user_agreement)
        btnUserAgreement.setOnClickListener {
            val userAgreementIntent =
                Intent(Intent.ACTION_VIEW, getString(R.string.user_agreement_website).toUri())
            startActivity(userAgreementIntent)
        }

    }
}