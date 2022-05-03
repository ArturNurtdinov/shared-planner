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
import ru.spbstu.calendar.databinding.EventFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.File
import ru.spbstu.common.di.FeatureUtils
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
        adapter.submitList(
            listOf(
                FileUi.FileUiItem(File(0, "1.pdf")),
                FileUi.FileUiItem(File(1, "2.pdf")),
                FileUi.FileUiItem(File(2, "3.pdf")),
                FileUi.FileUiItem(File(3, "4.pdf")),
                FileUi.FileUiItem(File(4, "5.pdf"))
            )
        )

        viewModel.event?.let {
            binding.fragmentEventColor.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4.dp.toFloat()
                setColor(Color.CYAN)
            }
            binding.fragmentEventTitle.text = it.title
            binding.fragmentEventGroup.text = it.group.title
            if (it.endTime != null && it.endTime != 0L) {
                binding.fragmentEventTime.text =
                    "${dateFormat.format(it.startTime)} - ${dateFormat.format(it.endTime)}"
            } else {
                binding.fragmentEventTime.text = dateFormat.format(it.startTime)
            }
            binding.fragmentEventDescription.isVisible = false
            binding.fragmentEventRepeat.isVisible = false
        }
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