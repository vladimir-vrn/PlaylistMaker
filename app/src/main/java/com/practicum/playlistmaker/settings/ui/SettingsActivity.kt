package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {
    private val viewModel by viewModel<SettingsViewModel>{
        parametersOf(
            this@SettingsActivity
        )
    }
    private lateinit var binding: ActivitySettingsBinding
    private var isSwtDarkThemeChangedManualMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.tbSettings.setNavigationOnClickListener {
            finish()
        }

        binding.swtDarkTheme.setOnCheckedChangeListener { switcher, checked ->
            if (isSwtDarkThemeChangedManualMode) viewModel.switchTheme(checked)
            isSwtDarkThemeChangedManualMode = true
        }

        binding.btnShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.btnSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.btnUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun showContent(nightMode: Boolean) {
        if (binding.swtDarkTheme.isChecked != nightMode) {
            isSwtDarkThemeChangedManualMode = false
            binding.swtDarkTheme.isChecked = nightMode
        }
    }

    private fun render(state: SettingsActivityState) {
        if (state is SettingsActivityState.Content) showContent(state.nightMode)
    }
}