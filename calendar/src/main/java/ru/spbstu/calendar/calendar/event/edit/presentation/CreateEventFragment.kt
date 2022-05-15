package ru.spbstu.calendar.calendar.event.edit.presentation

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.TimePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.calendar.domain.model.Notification
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.extensions.setDebounceClickListener
import java.io.File
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
        if (it == NotificationUi.AddNotification) {
            val notifsItems = getNotificationsTypes()
            MaterialAlertDialogBuilder(requireContext(), R.style.ShowGroupsDialogStyle)
                .setTitle(getString(R.string.repeat))
                .setItems(notifsItems) { dialog, which ->
                    viewModel.onNotifItemSelected(which)
                }
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        } else if (it is NotificationUi.NotificationUiItem) {
            viewModel.removeNotif(it)
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult
            viewModel.onNewFilePicked(uri)
        }

    private val filesAdapter = FilesAdapter {
        if (it == FileUi.AddFileItem) {
            getContent.launch("*/*")
        } else if (it is FileUi.FileUiItem) {
            kotlin.runCatching {
                val dir = requireContext().externalCacheDir ?: requireContext().externalCacheDirs[0]
                dir.mkdirs()
                val destination = File(dir, it.name + System.currentTimeMillis().toString())
                val request = DownloadManager.Request(it.uri).apply {
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationUri(Uri.fromFile(destination))
                }

                val dm =
                    requireContext().getSystemService(Activity.DOWNLOAD_SERVICE) as DownloadManager
                val id = dm.enqueue(request)
                viewModel.onNewDownloadStarted(id, Uri.fromFile(destination))
                Toast.makeText(
                    requireContext(),
                    getString(R.string.download_started),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

        requireArguments().getParcelable<EventModel?>(EVENT_KEY).run {
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
                currentSelected.monthValue - 1,
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
                currentSelected.monthValue - 1,
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
        binding.fragmentCreateEventNotifications.itemAnimator = null

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

        binding.fragmentCreateEventPickGroup.setDebounceClickListener {
            val state = viewModel.state.value
            val groups = state.groups.map { it.name }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext(), R.style.ShowGroupsDialogStyle)
                .setTitle(getString(R.string.pick_group))
                .setItems(groups) { dialog, which ->
                    viewModel.onGroupSelected(which)
                }
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.fragmentCreateEventInput.setText(
            if (viewModel.mode == CreateEventViewModel.Mode.CreateEvent) ""
            else (viewModel.mode as CreateEventViewModel.Mode.EditEvent).event!!.title,
            TextView.BufferType.EDITABLE
        )

        binding.fragmentCreateEventDescription.setText(
            if (viewModel.mode == CreateEventViewModel.Mode.CreateEvent) ""
            else (viewModel.mode as CreateEventViewModel.Mode.EditEvent).event!!.description,
            TextView.BufferType.EDITABLE
        )

        binding.fragmentCreateEventConfirm.setDebounceClickListener {
            val title =
                binding.fragmentCreateEventInput.text?.toString() ?: return@setDebounceClickListener
            if (title.length > binding.fragmentCreateEventInputLayout.counterMaxLength) {
                return@setDebounceClickListener
            }

            val description = binding.fragmentCreateEventDescription.text?.toString() ?: ""
            if (description.length > 250) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.description_restriction),
                    Toast.LENGTH_SHORT
                ).show()
                return@setDebounceClickListener
            }
            viewModel.onDoneAction(title, description)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.errorMessage
            .onEach {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }.launchIn(lifecycleScope)

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

                binding.fragmentCreateEventRepeatText.isClickable = !it.isEdit

                binding.fragmentCreateEventAllDaySwitch.isChecked = it.isAllDay

                binding.fragmentCreateEventFirstTime.isVisible = !it.isAllDay
                binding.fragmentCreateEventSecondDate.isVisible = !it.isReminder
                binding.fragmentCreateEventSecondTime.isVisible = !it.isAllDay && !it.isReminder

                binding.fragmentCreateEventRepeatText.text = getRepeatTextFromItem(it.repeatItem)

                binding.fragmentCreateEventFiles.isVisible = !it.isReminder && !it.isEdit
                binding.fragmentCreateEventFilesIcon.isVisible = !it.isReminder && !it.isEdit
                binding.fragmentCreateEventDivider7.isVisible = !it.isReminder
                binding.fragmentCreateEventDescriptionLayout.isVisible = !it.isReminder

                val notifs = it.notificationsTypes.map {
                    NotificationUi.NotificationUiItem(
                        Notification(
                            it.value,
                            getNotificationTextFromItem(it)
                        )
                    )
                }
                notifsAdapter.submitList(
                    mutableListOf<NotificationUi?>().apply {
                        addAll(notifs)
                        add(NotificationUi.AddNotification)
                    }
                )

                binding.fragmentCreateEventPickGroup.text =
                    if (it.selectedGroup != null) it.selectedGroup.name else getString(R.string.pick_group)
                val list = mutableListOf<FileUi>()
                it.files.forEachIndexed { index, value ->
                    list.add(FileUi.FileUiItem(value, it.pickedFiles[index]))
                }

                filesAdapter.submitList(
                    mutableListOf<FileUi?>().apply {
                        addAll(list)
                        if (list.size < 3 && !it.isEdit) {
                            add(FileUi.AddFileItem)
                        }
                    }
                )

                binding.fragmentCreateEventSpinner.selectItemByIndex(if (it.isReminder) 0 else 1)
            }
            .launchIn(lifecycleScope)
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