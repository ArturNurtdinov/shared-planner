package ru.spbstu.calendar.settings.presentation

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import ru.spbstu.calendar.databinding.FragmentSettingsBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import javax.inject.Inject

class SettingsFragment : Fragment() {

    @Inject
    lateinit var viewModel: SettingsViewModel

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        inject()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.onBackClicked()) {
                        requireActivity().finish()
                    }
                }
            })

        binding.fragmentSettingsBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        binding.fragmentCalendarProfile.setDebounceClickListener {
            viewModel.openProfile()
        }

        binding.fragmentCalendarNotifications.setDebounceClickListener {
            viewModel.openNotificationsSettings()
        }

        binding.fragmentCalendarGroups.setDebounceClickListener {
            viewModel.openGroupsPage()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .settingsComponentFactory()
            .create(this)
            .inject(this)
    }

}