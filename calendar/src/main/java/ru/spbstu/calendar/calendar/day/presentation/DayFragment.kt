package ru.spbstu.calendar.calendar.day.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.databinding.DayFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.common.R
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class DayFragment : Fragment() {
    @Inject
    lateinit var viewModel: DayViewModel

    private var _binding: DayFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = EventsAdapter {
        viewModel.goToEventPage(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DayFragmentBinding.inflate(layoutInflater, container, false)
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

        val dateFormat = SimpleDateFormat("EE, dd MMMM yyyy", Locale.getDefault())
        val date = requireArguments().getLong(DATE_KEY)
        val localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
        viewModel.date = localDate
        binding.fragmentDayTitle.text = dateFormat.format(Date(date))

        binding.fragmentDayEvents.adapter = adapter

        binding.fragmentDayBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state
            .onEach {
                if (it.loading) {
                    binding.fragmentDayShadow.isVisible = true
                    binding.fragmentDayProgressBar.isVisible = true
                } else {
                    binding.fragmentDayShadow.isVisible = false
                    binding.fragmentDayProgressBar.isVisible = false
                    if (it.events.isEmpty() && it.error) {
                        binding.fragmentDayEmpty.isVisible = true
                        binding.fragmentDayEmpty.text = getString(R.string.data_error)
                    } else if (it.events.isEmpty()) {
                        binding.fragmentDayEmpty.isVisible = true
                        binding.fragmentDayEmpty.text = getString(R.string.empty_day)
                        adapter.submitList(it.events)
                    } else {
                        binding.fragmentDayEmpty.isVisible = false
                        adapter.submitList(it.events)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .dayComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        const val DATE_KEY = "ru.spbstu.sharedplanner.DATE_KEY"
    }

}