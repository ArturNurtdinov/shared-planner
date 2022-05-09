package ru.spbstu.calendar.calendar.day.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import ru.spbstu.calendar.databinding.DayFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.calendar.domain.model.Group
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
        val localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
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
        adapter.submitList(
            listOf(Event(1, Date().time, Date().time, "Start work", Group(0, "Work", Color.WHITE,true, 25)))
        )
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