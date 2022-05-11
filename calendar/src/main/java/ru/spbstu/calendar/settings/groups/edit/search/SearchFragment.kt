package ru.spbstu.calendar.settings.groups.edit.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.spbstu.calendar.databinding.SearchFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantUi
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantsAdapter
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import ru.spbstu.common.network.PictureUrlHelper
import javax.inject.Inject

class SearchFragment : Fragment() {
    @Inject
    lateinit var viewModel: SearchViewModel

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var urlHelper: PictureUrlHelper

    private lateinit var addedUsersAdapter: ParticipantsAdapter

    private val foundUsersAdapter = ParticipantSearchAdapter {
        viewModel.addUser(it)
        refreshFound()
    }

    private fun refreshFound() {
        foundUsersAdapter.refresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(layoutInflater, container, false)
        inject()

        addedUsersAdapter = ParticipantsAdapter(urlHelper) {
            viewModel.deleteUser((it as ParticipantUi.ParticipantUiItem).profile)
            refreshFound()
        }
        addedUsersAdapter.isForSearch = true

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.onBackPressed()) {
                        requireActivity().finish()
                    }
                }
            })

        val merged = ConcatAdapter(foundUsersAdapter, addedUsersAdapter)
        binding.fragmentSearchParticipantsFound.adapter = merged
//        binding.fragmentSearchParticipantsAdded.adapter = addedUsersAdapter

//        binding.fragmentSearchParticipantsLabel.isVisible = false

//        findNavController().previousBackStackEntry?.savedStateHandle?.set("key", bundleOf())

        binding.fragmentSearchClear.setDebounceClickListener {
            binding.fragmentSearchInput.setText("", TextView.BufferType.EDITABLE)
        }

        binding.fragmentSearchInput.addTextChangedListener {
            binding.fragmentSearchClear.isVisible = !it.isNullOrEmpty()
            val query = it?.toString() ?: return@addTextChangedListener
            viewModel.query = query
            foundUsersAdapter.refresh()
        }

        binding.fragmentSearchDone.setDebounceClickListener {
            val added = viewModel.getAdded().toTypedArray()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                RESULT_KEY, bundleOf(
                    RESULT_DATA_KEY to added
                )
            )
            viewModel.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state
            .onEach {
                addedUsersAdapter.submitList(it.added)
//                foundUsersAdapter.submitList(it.found)
//                binding.fragmentSearchParticipantsLabel.isVisible = it.added.isNotEmpty()
            }
            .launchIn(lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchFlow.collectLatest { pagingData ->
                foundUsersAdapter.submitData(pagingData)
            }
        }
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

    companion object {
        const val RESULT_KEY = "ru.spbstu.sharedplanned.search.RESULT_KEY"
        const val RESULT_DATA_KEY = "ru.spbstu.sharedplanned.search.RESULT_DATA_KEY"
    }

}