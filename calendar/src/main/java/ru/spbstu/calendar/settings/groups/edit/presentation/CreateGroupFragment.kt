package ru.spbstu.calendar.settings.groups.edit.presentation

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.CreateGroupFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantUi
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantsAdapter
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.hideKeyboard
import ru.spbstu.common.extensions.setDebounceClickListener
import ru.spbstu.common.extensions.showKeyboard
import javax.inject.Inject


class CreateGroupFragment : Fragment() {
    @Inject
    lateinit var viewModel: CreateGroupViewModel

    private var _binding: CreateGroupFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = ParticipantsAdapter {
        when (it) {
            is ParticipantUi.AddParticipant -> {
                viewModel.openSearch()
            }
            is ParticipantUi.ParticipantUiItem -> {

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateGroupFragmentBinding.inflate(layoutInflater, container, false)
        inject()

        viewModel.mode = if (arguments?.containsKey(GROUP_KEY) == true) {
            CreateGroupViewModel.Mode.EditGroup(requireArguments().getParcelable(GROUP_KEY)!!)
        } else {
            CreateGroupViewModel.Mode.CreateGroup
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.fragmentCreateGroupInput.hideKeyboard()
                    if (!viewModel.onBackPressed()) {
                        requireActivity().finish()
                    }
                }
            })

        binding.fragmentCreateGroupBack.setDebounceClickListener {
            binding.fragmentCreateGroupInput.hideKeyboard()
            if (!viewModel.onBackPressed()) {
                requireActivity().finish()
            }
        }

        binding.fragmentCreateGroupEditName.setDebounceClickListener {
            binding.fragmentCreateGroupInput.isEnabled = true
            binding.fragmentCreateGroupInput.requestFocus()
            binding.fragmentCreateGroupInput.showKeyboard()
            binding.fragmentCreateGroupDone.isVisible = true
            it.isVisible = false
        }

        binding.fragmentCreateGroupParticipants.adapter = adapter
        binding.fragmentCreateGroupParticipants.setHasFixedSize(false)

        binding.fragmentCreateGroupColorPicker.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 4.dp.toFloat()
            setColor(Color.CYAN)
        }

        binding.fragmentCreateGroupColorPicker.setDebounceClickListener {
            ColorPickerDialog()
                .withColor(Color.CYAN) // the default / initial color
                .withAlphaEnabled(false)
                .withListener { dialog, color ->
                    // a color has been picked; use it
                    binding.fragmentCreateGroupColorPicker.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 4.dp.toFloat()
                        setColor(color)
                    }
                }
                .show(childFragmentManager, "colorPicker")
        }

//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle?>("key")
//            ?.observe(viewLifecycleOwner) { result ->
//                Log.d("WWWW", "Search result")
//            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mode = viewModel.mode!!
        when (mode) {
            is CreateGroupViewModel.Mode.CreateGroup -> {
                binding.fragmentCreateGroupInput.isEnabled = true
                binding.fragmentCreateGroupInput.requestFocus()
                binding.fragmentCreateGroupInput.showKeyboard()
                binding.fragmentCreateGroupInputLayout.isCounterEnabled = true
                binding.fragmentCreateGroupDone.isVisible = true
                binding.fragmentCreateGroupEditName.isVisible = false
            }
            is CreateGroupViewModel.Mode.EditGroup -> {
                binding.fragmentCreateGroupInput.isEnabled = false
                binding.fragmentCreateGroupDone.isVisible = false
                binding.fragmentCreateGroupEditName.isVisible = true
                binding.fragmentCreateGroupInputLayout.isCounterEnabled = false
                binding.fragmentCreateGroupInput.setText(mode.group.title)
            }
        }

        binding.fragmentCreateGroupParticipantsLabel.isVisible = true
        binding.fragmentCreateGroupParticipantsLabel.text =
            resources.getQuantityString(R.plurals.participants, 25, 25)

        adapter.submitList(
            listOf(
                ParticipantUi.ParticipantUiItem(
                    Profile(
                        0,
                        "Artur Nurtdinov",
                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
//                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    )
                ),
                ParticipantUi.ParticipantUiItem(
                    Profile(
                        0,
                        "Artur Nurtdinov",
//                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    )
                ),
                ParticipantUi.AddParticipant,
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .createGroupComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        const val GROUP_KEY = "ru.spbstu.sharedplanner.GROUP_KEY"
    }
}