package ru.spbstu.calendar.settings.notifications.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.NotificationsFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import javax.inject.Inject

class NotificationsFragment : Fragment() {
    @Inject
    lateinit var viewModel: NotificationsViewModel

    private var _binding: NotificationsFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = NotificationsGroupsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationsFragmentBinding.inflate(layoutInflater, container, false)
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

        binding.fragmentNotificationsBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        binding.fragmentNotificationsNotifications.layoutNotificationTitle.text =
            getString(R.string.notifications)

        binding.fragmentNotificationsNotifications.root.setDebounceClickListener {
            binding.fragmentNotificationsNotifications.layoutNotificationSwitch.isChecked =
                !binding.fragmentNotificationsNotifications.layoutNotificationSwitch.isChecked
        }

        binding.fragmentNotificationsGroups.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.submitList(
            listOf(
                Group(1, "Friends", true, 0),
                Group(1, "Family", true, 1),
                Group(1, "Work", false, 25),
            )
        ) {
            binding.fragmentNotificationsEmptyTitle.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .notificationsComponentFactory()
            .create(this)
            .inject(this)
    }

}