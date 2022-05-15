package ru.spbstu.calendar.calendar.event.presentation

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.setDebounceClickListener
import java.io.File
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
            is FileUi.AddFileItem -> {
                // not used
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: return
            if (id != -1L) {
                /*viewModel.getUriForId(id)?.let { uri ->
                    requireContext().startActivity(Intent(Intent.ACTION_VIEW).apply {
                        setData(uri)
                    })
                }*/
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

        binding.fragmentEventDelete.setDebounceClickListener {
            viewModel.deleteEvent()
        }

        requireActivity().registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.errorMessage
            .onEach {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
            .launchIn(lifecycleScope)

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
                if (it.allDay) {
                    binding.fragmentEventTime.text = getString(R.string.all_day)
                } else {
                    if (it.from.isBefore(it.to)) {
                        binding.fragmentEventTime.text =
                            "${dateFormat.format(it.from.toInstant().toEpochMilli())} - " +
                                    "${dateFormat.format(it.to.toInstant().toEpochMilli())}"
                    } else {
                        binding.fragmentEventTime.text =
                            dateFormat.format(it.to.toInstant().toEpochMilli())
                    }
                }
                binding.fragmentEventDescription.isVisible =
                    state.eventModel.eventType == EventTypes.EVENT && it.description.isNotEmpty()
                binding.fragmentEventRepeat.isVisible = true
                binding.fragmentEventRepeat.text = getRepeatTextFromItem(it.repeatType)
                binding.fragmentEventDescription.text = it.description

                binding.fragmentEventFilesLabel.isVisible = it.attaches.isNotEmpty()
                val list = mutableListOf<FileUi>()
                it.fileNames.forEachIndexed { index, value ->
                    list.add(FileUi.FileUiItem(value, it.attaches[index]))
                }
                adapter.submitList(list)
            }
            .launchIn(lifecycleScope)
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
        requireActivity().unregisterReceiver(receiver)
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