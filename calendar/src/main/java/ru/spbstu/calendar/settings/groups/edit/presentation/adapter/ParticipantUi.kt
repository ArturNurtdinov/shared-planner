package ru.spbstu.calendar.settings.groups.edit.presentation.adapter

import ru.spbstu.calendar.domain.model.Profile

sealed class ParticipantUi {
    abstract val viewType: Int

    data class ParticipantUiItem(val profile: Profile) : ParticipantUi() {
        override val viewType: Int = PARTICIPANT_UI_ITEM_VIEW_TYPE
    }

    object AddParticipant : ParticipantUi() {
        override val viewType: Int = ADD_PARTICIPANT_VIEW_TYPE
    }

    companion object {
        const val PARTICIPANT_UI_ITEM_VIEW_TYPE = 1823
        const val ADD_PARTICIPANT_VIEW_TYPE = 3219
    }
}