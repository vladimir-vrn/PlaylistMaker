package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>{
        parametersOf(requireContext())
    }
    private lateinit var binding: FragmentSettingsBinding
    private var isSwtDarkThemeChangedManualMode = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
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

    private fun render(state: SettingsState) {
        if (state is SettingsState.Content) showContent(state.nightMode)
    }
}