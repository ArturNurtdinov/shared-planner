package ru.spbstu.calendar.settings.groups.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.databinding.GroupsFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.settings.groups.presentation.model.GroupUi
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import javax.inject.Inject

class GroupsFragment : Fragment() {

    @Inject
    lateinit var viewModel: GroupsViewModel

    private var _binding: GroupsFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = GroupsAdapter {
        when (it) {
            is GroupUi.CreateGroupItem -> {
                viewModel.openCreateGroupPage()
            }
            is GroupUi.GroupUiItem -> {
                viewModel.openEditGroupPage(it.group)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupsFragmentBinding.inflate(layoutInflater, container, false)
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

        binding.fragmentGroupsBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        binding.fragmentGroupsGroups.adapter = adapter

        viewModel.loadData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state
            .onEach {
                adapter.submitList(it.groups)
            }
            .launchIn(lifecycleScope)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .groupsComponentFactory()
            .create(this)
            .inject(this)
    }

}