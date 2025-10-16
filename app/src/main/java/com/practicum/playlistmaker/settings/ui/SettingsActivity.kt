package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(this@SettingsActivity)
        ).get(SettingsViewModel::class.java)

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.tbSettings.setNavigationOnClickListener {
            finish()
        }

        binding.swtDarkTheme.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
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
            binding.swtDarkTheme.isChecked = nightMode
        }
    }

    private fun render(state: SettingsActivityState) {
        if (state is SettingsActivityState.Content) showContent(state.nightMode)
    }
}