package ru.spbstu.calendar.calendar.event.edit.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.R
import ru.spbstu.calendar.calendar.event.presentation.FileUi
import ru.spbstu.calendar.calendar.event.presentation.FilesAdapter
import ru.spbstu.calendar.databinding.CreateEventFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.calendar.domain.model.File
import ru.spbstu.calendar.domain.model.Notification
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class CreateEventFragment : Fragment() {

    @Inject
    lateinit var viewModel: CreateEventViewModel

    private var _binding: CreateEventFragmentBinding? = null
    private val binding get() = _binding!!

    private val notifsAdapter = NotificationsAdapter {

    }

    private val filesAdapter = FilesAdapter {

    }

    private val dateFormat = SimpleDateFormat("EE, dd MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateEventFragmentBinding.inflate(layoutInflater, container, false)
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

        binding.fragmentCreateEventBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        requireArguments().getParcelable<Event?>(EVENT_KEY).run {
            if (this == null) {
                viewModel.mode = CreateEventViewModel.Mode.CreateEvent
            } else {
                viewModel.mode = CreateEventViewModel.Mode.EditEvent(this)
            }
        }

        val items = resources.getStringArray(R.array.events)

        binding.fragmentCreateEventFirstDate.setDebounceClickListener {
            val currentSelected = viewModel.state.value.selectedDateFirst
            DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    viewModel.onFirstDateSelected(year, month + 1, dayOfMonth)
                },
                currentSelected.year,
                currentSelected.monthValue,
                currentSelected.dayOfMonth
            ).show()
        }

        binding.fragmentCreateEventSecondDate.setDebounceClickListener {
            val currentSelected = viewModel.state.value.selectedDateSecond
            DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    viewModel.onSecondDateSelected(year, month + 1, dayOfMonth)
                },
                currentSelected.year,
                currentSelected.monthValue,
                currentSelected.dayOfMonth
            ).show()
        }

        binding.fragmentCreateEventSpinner.apply {
            showDivider = true
            spinnerPopupBackgroundColor = Color.WHITE
            dividerColor =
                ResourcesCompat.getColor(resources, R.color.rippleColor, requireActivity().theme)
            lifecycleOwner = this@CreateEventFragment.viewLifecycleOwner
            setItems(items.toList())
            spinnerOutsideTouchListener =
                OnSpinnerOutsideTouchListener { _, _ -> this.dismiss() }
            setOnSpinnerItemSelectedListener { oldIndex, oldItem: String?, newIndex, newItem: String? ->
                viewModel.selectedIndex = newIndex
            }
            selectItemByIndex(viewModel.selectedIndex)
        }

        binding.fragmentCreateEventAllDaySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onNewAllDayValue(isChecked)
        }

        binding.fragmentCreateEventNotifications.adapter = notifsAdapter

        binding.fragmentCreateEventFiles.adapter = filesAdapter

        binding.fragmentCreateEventFirstTime.setDebounceClickListener {
            val currentState = viewModel.state.value
            TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minutes ->
                    viewModel.onFirstTimeSelected(hourOfDay, minutes)
                },
                currentState.selectedTimeFirst.hour,
                currentState.selectedTimeFirst.minute,
                true
            ).show()
        }

        binding.fragmentCreateEventSecondTime.setDebounceClickListener {
            val currentState = viewModel.state.value
            TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minutes ->
                    viewModel.onSecondTimeSelected(hourOfDay, minutes)
                },
                currentState.selectedTimeSecond.hour,
                currentState.selectedTimeSecond.minute,
                true
            ).show()
        }

        binding.fragmentCreateEventRepeatText.setDebounceClickListener {
            val dialogItems = getRepeatItems()
            MaterialAlertDialogBuilder(requireContext(), R.style.ShowGroupsDialogStyle)
                .setTitle(getString(R.string.repeat))
                .setItems(dialogItems) { dialog, which ->
                    viewModel.onRepeatItemSelected(which)
                }
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notifsAdapter.submitList(
            listOf(
                NotificationUi.NotificationUiItem(
                    Notification(
                        0,
                        "За 15 минут"
                    )
                ), NotificationUi.AddNotification
            )
        )

        filesAdapter.submitList(
            listOf(
                FileUi.FileUiItem(File(0, "kek.pdf")),
                FileUi.AddFileItem
            )
        )

        viewModel.state
            .onEach {
                binding.fragmentCreateEventFirstDate.text = dateFormat.format(
                    Date(
                        it.selectedDateFirst.atStartOfDay().atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                    )
                )
                binding.fragmentCreateEventSecondDate.text = dateFormat.format(
                    Date(
                        it.selectedDateSecond.atStartOfDay().atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                    )
                )

                binding.fragmentCreateEventFirstTime.text = timeFormat.format(
                    it.selectedTimeFirst.atZone(
                        ZoneId.systemDefault()
                    ).toInstant().toEpochMilli()
                )
                binding.fragmentCreateEventSecondTime.text =
                    timeFormat.format(
                        it.selectedTimeSecond.atZone(
                            ZoneId.systemDefault()
                        ).toInstant().toEpochMilli()
                    )

                binding.fragmentCreateEventAllDaySwitch.isChecked = it.isAllDay

                binding.fragmentCreateEventFirstTime.isVisible = !it.isAllDay
                binding.fragmentCreateEventSecondDate.isVisible = !it.isReminder
                binding.fragmentCreateEventSecondTime.isVisible = !it.isAllDay && !it.isReminder

                binding.fragmentCreateEventRepeatText.text = getRepeatTextFromItem(it.repeatItem)

                binding.fragmentCreateEventFiles.isVisible = !it.isReminder
                binding.fragmentCreateEventFilesIcon.isVisible = !it.isReminder
                binding.fragmentCreateEventDivider7.isVisible = !it.isReminder
                binding.fragmentCreateEventDescriptionLayout.isVisible = !it.isReminder
            }
            .launchIn(lifecycleScope)
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

    private fun getRepeatTextFromItem(repeatItem: CreateEventViewModel.RepeatItem): String {
        return when (repeatItem) {
            CreateEventViewModel.RepeatItem.Every3Days -> getString(R.string.every_3_days)
            CreateEventViewModel.RepeatItem.EveryDay -> getString(R.string.every_day)
            CreateEventViewModel.RepeatItem.EveryMonth -> getString(R.string.every_month)
            CreateEventViewModel.RepeatItem.EveryWeek -> getString(R.string.every_week)
            CreateEventViewModel.RepeatItem.EveryYear -> getString(R.string.every_year)
            CreateEventViewModel.RepeatItem.None -> getString(R.string.doesnt_repeat)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .createEventComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        const val EVENT_KEY = "ru.spbstu.sharedplanner.EVENT_KEY"
    }

}