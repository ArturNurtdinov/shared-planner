package ru.spbstu.calendar.calendar.event.presentation

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.EventFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.setDebounceClickListener
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventFragment : Fragment() {

    @Inject
    lateinit var viewModel: EventViewModel

    private var _binding: EventFragmentBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("EE, dd MMMM HH:mm", Locale.getDefault())

    private val adapter = FilesAdapter {
        when (it) {
            is FileUi.FileUiItem -> {
                // download and open intent
            }
            is FileUi.AddFileItem -> {
                // not used
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EventFragmentBinding.inflate(layoutInflater, container, false)
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

        binding.fragmentEventBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        binding.fragmentEventFiles.adapter = adapter

        viewModel.event = arguments?.getParcelable(EVENT_KEY)

        binding.fragmentEventEdit.setDebounceClickListener {
            viewModel.editEvent()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.state
            .filterNotNull()
            .onEach { state ->
                val it = state.eventModel
                binding.fragmentEventColor.background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 4.dp.toFloat()
                    setColor(it.group.color)
                }
                binding.fragmentEventTitle.text = it.title
                binding.fragmentEventGroup.text = it.group.name
                if (it.from.isBefore(it.to)) {
                    binding.fragmentEventTime.text =
                        "${
                            dateFormat.format(
                                it.from.toInstant().toEpochMilli()
                            )
                        } - ${dateFormat.format(it.to.toInstant().toEpochMilli())}"
                } else {
                    binding.fragmentEventTime.text =
                        dateFormat.format(it.to.toInstant().toEpochMilli())
                }
                binding.fragmentEventDescription.isVisible = state.eventModel.eventType == EventTypes.EVENT
                binding.fragmentEventRepeat.isVisible = true
                binding.fragmentEventRepeat.text = getRepeatTextFromItem(it.repeatType)
                binding.fragmentEventDescription.text = it.description

                binding.fragmentEventFilesLabel.isVisible = it.attaches.isNotEmpty()
                adapter.submitList(it.fileNames.map {
                    FileUi.FileUiItem(it)
                })
            }
            .launchIn(lifecycleScope)
    }

    private fun getNotificationTextFromItem(repeatItem: NotificationsTypes): String {
        return when (repeatItem) {
            NotificationsTypes.MIN_5 -> getString(R.string.minutes_5)
            NotificationsTypes.MIN_10 -> getString(R.string.minutes_10)
            NotificationsTypes.MIN_15 -> getString(R.string.minutes_15)
            NotificationsTypes.MIN_30 -> getString(R.string.minutes_30)
            NotificationsTypes.HOUR -> getString(R.string.in_hour)
            NotificationsTypes.DAY -> getString(R.string.in_day)
        }
    }


    private fun getRepeatItems(): Array<String> {
        return arrayOf(
            getString(R.string.doesnt_repeat),
            getString(R.string.every_day),
            getString(R.string.every_3_days),
            getString(R.string.every_week),
            getString(R.string.every_month),
            getString(R.string.every_year),
        )
    }

    private fun getRepeatTextFromItem(repeatItem: RepeatTypes): String {
        return when (repeatItem) {
            RepeatTypes.EVERY_THREE_DAYS -> getString(R.string.every_3_days)
            RepeatTypes.NONE -> getString(R.string.doesnt_repeat)
            RepeatTypes.EVERY_DAY -> getString(R.string.every_day)
            RepeatTypes.EVERY_WEEK -> getString(R.string.every_week)
            RepeatTypes.EVERY_MONTH -> getString(R.string.every_month)
            RepeatTypes.EVERY_YEAR -> getString(R.string.every_year)
        }
    }

    private fun getNotificationsTypes(): Array<String> {
        return arrayOf(
            getString(R.string.minutes_5),
            getString(R.string.minutes_10),
            getString(R.string.minutes_15),
            getString(R.string.minutes_30),
            getString(R.string.in_hour),
            getString(R.string.in_day),
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .eventComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        const val EVENT_KEY = "ru.spbstu.sharedplanner.EVENT_KEY"
    }

}