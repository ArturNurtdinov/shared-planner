package ru.spbstu.calendar.settings.groups.edit.search

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
import ru.spbstu.calendar.databinding.SearchFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantsAdapter
import ru.spbstu.common.di.FeatureUtils
import javax.inject.Inject

class SearchFragment : Fragment() {
    @Inject
    lateinit var viewModel: SearchViewModel

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private val addedUsersAdapter = ParticipantsAdapter {

    }

    private val foundUsersAdapter = ParticipantSearchAdapter {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(layoutInflater, container, false)
        inject()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.onBackPressed()) {
                        requireActivity().finish()
                    }
                }
            })

        binding.fragmentSearchParticipantsFound.adapter = foundUsersAdapter
        binding.fragmentSearchParticipantsAdded.adapter = addedUsersAdapter

        binding.fragmentSearchParticipantsLabel.isVisible = false

//        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", bundleOf())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state
            .onEach {
                addedUsersAdapter.submitList(it.added)
                foundUsersAdapter.submitList(it.found)
                binding.fragmentSearchParticipantsLabel.isVisible = it.added.isNotEmpty()
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .searchComponentFactory()
            .create(this)
            .inject(this)
    }

}