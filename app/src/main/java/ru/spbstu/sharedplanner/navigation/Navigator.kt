package ru.spbstu.sharedplanner.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import ru.spbstu.auth.AuthRouter
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.day.presentation.DayFragment
import ru.spbstu.calendar.calendar.event.edit.presentation.CreateEventFragment
import ru.spbstu.calendar.calendar.event.presentation.EventFragment
import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.settings.groups.edit.presentation.CreateGroupFragment
import ru.spbstu.sharedplanner.R
import ru.spbstu.sharedplanner.root.presentation.RootRouter

class Navigator : RootRouter, CalendarRouter, AuthRouter {
    private var navController: NavController? = null
    private var activity: AppCompatActivity? = null

    fun attach(navController: NavController, activity: AppCompatActivity) {
        this.navController = navController
        this.activity = activity
    }

    fun detach() {
        navController = null
        activity = null
    }

    override fun goToAuthPage() {
        navController?.navigate(R.id.action_mainFragment_to_authFragment)
    }

    override fun goToCalendarPage() {
        navController?.navigate(R.id.action_mainFragment_to_calendarFragment)
    }

    override fun pop(): Boolean {
        return navController?.popBackStack() == true
    }

    override fun openMainPage() {
        navController?.navigate(R.id.action_authFragment_to_calendarFragment)
    }

    override fun openSettings() {
        navController?.navigate(R.id.action_calendarFragment_to_settingFragment)
    }

    override fun openProfile() {
        navController?.navigate(R.id.action_settingFragment_to_profileFragment)
    }

    override fun openNotificationsSettings() {
        navController?.navigate(R.id.action_settingFragment_to_notificationsFragment)
    }

    override fun openGroupsPage() {
        navController?.navigate(R.id.action_settingFragment_to_groupsFragment)
    }

    override fun openSettingsFromCalendar() {
        navController?.navigate(R.id.action_calendarFragment_to_groupsFragment)
    }

    override fun goToGroupCreateOrEdit(group: Group?) {
        if (group != null) {
            navController?.navigate(
                R.id.action_groupsFragment_to_createGroupFragment,
                bundleOf(CreateGroupFragment.GROUP_KEY to group)
            )
        } else {
            navController?.navigate(R.id.action_groupsFragment_to_createGroupFragment)
        }
    }

    override fun openSearch() {
        navController?.navigate(R.id.action_createGroupFragment_to_searchFragment)
    }

    override fun openDayPage(date: Long) {
        navController?.navigate(
            R.id.action_calendarFragment_to_dayFragment,
            bundleOf(DayFragment.DATE_KEY to date)
        )
    }

    override fun openEventFragment(event: Event) {
        navController?.navigate(
            R.id.action_dayFragment_to_eventFragment,
            bundleOf(EventFragment.EVENT_KEY to event)
        )
    }

    override fun openCreateEventFragment(event: Event?) {
        if (event != null) {
            navController?.navigate(
                R.id.action_eventFragment_to_createEventFragment,
                bundleOf(CreateEventFragment.EVENT_KEY to event)
            )
        } else {
            navController?.navigate(
                R.id.action_calendarFragment_to_createEventFragment,
                bundleOf(CreateEventFragment.EVENT_KEY to event)
            )
        }
    }

    override fun goToLogin() {
        while (navController?.popBackStack() == true) {
        }
        navController?.navigate(R.id.authFragment)
    }
}