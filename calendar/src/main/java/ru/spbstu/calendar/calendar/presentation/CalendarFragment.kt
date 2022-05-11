package ru.spbstu.calendar.calendar.presentation

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.kizitonwose.calendarview.utils.Size
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.R
import ru.spbstu.calendar.calendar.presentation.dialog.ShowGroupsDialogFragment
import ru.spbstu.calendar.calendar.presentation.dialog.ShowGroupsDialogFragment.Companion.CONFIGURE_ACTION
import ru.spbstu.calendar.calendar.presentation.dialog.ShowGroupsDialogFragment.Companion.CONFIGURE_KEY
import ru.spbstu.calendar.calendar.presentation.dialog.ShowGroupsDialogFragment.Companion.GROUPS_RESULT_KEY
import ru.spbstu.calendar.calendar.presentation.dialog.ShowGroupsDialogFragment.Companion.RESULT_KEY
import ru.spbstu.calendar.calendar.presentation.dialog.model.GroupSelected
import ru.spbstu.calendar.databinding.CalendarFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.daysOfWeekFromLocale
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.setDebounceClickListener
import java.time.DateTimeException
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

class CalendarFragment : Fragment() {
    @Inject
    lateinit var viewModel: CalendarViewModel

    private var _binding: CalendarFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var years: List<Int>

    private var dialog: ShowGroupsDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarFragmentBinding.inflate(layoutInflater, container, false)
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

        childFragmentManager.setFragmentResultListener(
            RESULT_KEY,
            viewLifecycleOwner
        ) { key, result ->
            if (result.getString(CONFIGURE_KEY) == CONFIGURE_ACTION) {
                viewModel.navigateToGroupSettings()
            } else if (result.containsKey(GROUPS_RESULT_KEY)) {
                val newGroups = result.getParcelableArray(GROUPS_RESULT_KEY) as Array<GroupSelected>
                viewModel.onSelectedChange(newGroups.toList())
            }
        }

        binding.fragmentCalendarGroups.setDebounceClickListener {
            if (dialog?.isVisible == true) return@setDebounceClickListener
            val groups = arrayListOf<GroupSelected>().apply {
                addAll(viewModel.state.value.groups)
            }
            if (groups.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_groups), Toast.LENGTH_SHORT)
                    .show()
                return@setDebounceClickListener
            }
            dialog = ShowGroupsDialogFragment.newInstance(groups)
            dialog?.show(childFragmentManager, ShowGroupsDialogFragment::class.java.simpleName)
        }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(100)
        val lastMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val dm = Resources.getSystem().displayMetrics
        binding.fragmentCalendarCalendar.apply {
            val dayWidth = dm.widthPixels / 7
            val dayHeight = (dm.heightPixels - 200.dp) / maxRowCount
            daySize = Size(dayWidth, dayHeight)
            setup(firstMonth, lastMonth, firstDayOfWeek)
            if (viewModel.shouldScrollOnCreate) {
                viewModel.shouldScrollOnCreate = false
                scrollToMonth(currentMonth)
            }
            monthScrollListener = object : MonthScrollListener {
                override fun invoke(p1: CalendarMonth) {
                    viewModel.isManuallySelectedMonth = true
                    binding.fragmentCalendarMonth.selectItemByIndex(p1.month - 1)
                    val selectedYear = years[binding.fragmentCalendarYear.selectedIndex]
                    if (selectedYear != p1.year && !viewModel.isManuallySelectedYear) {
                        viewModel.isManuallySelectedYear = true
                        binding.fragmentCalendarYear.selectItemByIndex(years.indexOf(p1.year))
                    }
                }
            }
        }

        val months = resources.getStringArray(R.array.months_array)

        binding.fragmentCalendarMonth.apply {
            showDivider = true
            spinnerPopupBackgroundColor = Color.WHITE
            dividerColor =
                ResourcesCompat.getColor(resources, R.color.rippleColor, requireActivity().theme)
            lifecycleOwner = this@CalendarFragment.viewLifecycleOwner
            setItems(months.toList())
            selectItemByIndex(viewModel.today.monthValue - 1)
            spinnerOutsideTouchListener =
                OnSpinnerOutsideTouchListener { _, _ -> this.dismiss() }
            setOnSpinnerItemSelectedListener { oldIndex, oldItem: String?, newIndex, newItem: String ->
                if (viewModel.isManuallySelectedMonth) {
                    viewModel.isManuallySelectedMonth = false
                    return@setOnSpinnerItemSelectedListener
                }
                val selectedYear = years[binding.fragmentCalendarYear.selectedIndex]
                val newMonth = months.indexOf(newItem) + 1
                try {
                    val date = LocalDate.of(
                        selectedYear,
                        newMonth,
                        viewModel.today.dayOfMonth
                    )
                    binding.fragmentCalendarCalendar.scrollToDate(date)
                    viewModel.onScolledToNewDate(date)
                } catch (e: DateTimeException) {
                    val date = LocalDate.of(
                        selectedYear,
                        newMonth,
                        1
                    )
                    binding.fragmentCalendarCalendar.scrollToDate(date)
                    viewModel.onScolledToNewDate(date)
                }
            }
        }

        val currentYear = viewModel.today.year
        years = mutableListOf<Int>().apply {
            for (i in -10..10) {
                add(currentYear + i)
            }
        }
        binding.fragmentCalendarYear.apply {
            showDivider = true
            spinnerPopupBackgroundColor = Color.WHITE
            dividerColor =
                ResourcesCompat.getColor(resources, R.color.rippleColor, requireActivity().theme)
            lifecycleOwner = this@CalendarFragment.viewLifecycleOwner
            setItems(years.map { it.toString() })
            val currentYearIndex = years.indexOf(currentYear)
            selectItemByIndex(currentYearIndex)
            spinnerOutsideTouchListener =
                OnSpinnerOutsideTouchListener { _, _ -> this.dismiss() }
            setOnSpinnerItemSelectedListener { oldIndex, oldItem: String?, newIndex, newItem: String ->
                if (viewModel.isManuallySelectedYear) {
                    viewModel.isManuallySelectedYear = false
                    return@setOnSpinnerItemSelectedListener
                }
                val selectedMonth = binding.fragmentCalendarMonth.selectedIndex + 1
                try {
                    val date = LocalDate.of(
                        newItem.toInt(),
                        selectedMonth,
                        viewModel.today.dayOfMonth
                    )
                    binding.fragmentCalendarCalendar.scrollToDate(date)
                    viewModel.onScolledToNewDate(date)
                } catch (e: DateTimeException) {
                    val date = LocalDate.of(
                        newItem.toInt(),
                        selectedMonth,
                        1
                    )
                    binding.fragmentCalendarCalendar.scrollToDate(date)
                    viewModel.onScolledToNewDate(date)
                }
            }
        }
        val daysOfWeek = daysOfWeekFromLocale()

        binding.fragmentCalendarLegendLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .uppercase(Locale.ENGLISH)
                setTextColor(Color.BLACK)
            }
        }

        binding.fragmentCalendarSettings.setDebounceClickListener {
            viewModel.openSettings()
        }

        binding.fragmentCalendarFab.setDebounceClickListener {
            viewModel.openCreateEventPage()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state
            .onEach {
                Log.d("WWWW", "$it")
            }
            .launchIn(lifecycleScope)
        binding.fragmentCalendarCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.root.setDebounceClickListener {
                    viewModel.openDay(
                        day.date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli()
                    )
                }
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.root.alpha = 1f
                    when (day.date) {
                        viewModel.today -> {
                            container.dayText.setTextColor(Color.BLACK)
                            container.root.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.secondary
                                )
                            )
                        }
                        else -> {
                            container.dayText.setTextColor(Color.BLACK)
                            container.root.setBackgroundColor(Color.WHITE)
                        }
                    }
                } else {
                    container.root.alpha = 0.5f
                    container.dayText.setTextColor(Color.BLACK)
                    container.root.setBackgroundColor(Color.WHITE)
                }

                container.dayText.text = day.date.dayOfMonth.toString()

                container.circles.forEach {
                    it.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(Color.CYAN)
                        cornerRadius = 4.dp.toFloat()
                    }
                }
            }

            override fun create(view: View): DayViewContainer = DayViewContainer(view)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .calendarComponentFactory()
            .create(this)
            .inject(this)
    }
}