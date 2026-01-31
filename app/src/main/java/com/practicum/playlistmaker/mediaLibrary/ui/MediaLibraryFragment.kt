package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.tabs.TabLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val viewModel by viewModel<MediaLibraryViewModel>()
    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (binding.viewPager.currentItem != tab.position)
                    viewModel.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.tbMediaLibrary.setNavigationOnClickListener {
            binding.tbMediaLibrary.navigationIcon = null
            setFragmentResult(
                PlaylistsFragment.GO_BACK_MEDIA_LIBRARY_KEY,
                bundleOf(PlaylistsFragment.GO_BACK_MEDIA_LIBRARY_KEY_DATA to true)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showArrow() {
        binding.tbMediaLibrary.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_left_arrow)
    }

    private fun render(state: MediaLibraryState) {
        if (state is MediaLibraryState.Content) {
            if (binding.tabLayout.selectedTabPosition != state.currentItem)
                binding.tabLayout.getTabAt(state.currentItem)?.select()
            if (binding.viewPager.currentItem != state.currentItem)
                binding.viewPager.currentItem = state.currentItem
        }
    }
}