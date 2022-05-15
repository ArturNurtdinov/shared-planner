package ru.spbstu.calendar.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.network.model.EventResponse
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class EventModel(
    val id: String,
    val group: Group,
    val eventType: EventTypes,
    val title: String,
    val description: String,
    val allDay: Boolean,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val repeatType: RepeatTypes,
    val notifications: List<NotificationsTypes>,
    val attaches: List<Uri>,
    val fileNames: List<String>,
) : Parcelable {

    companion object {
        fun fromNetworkModel(eventResponse: EventResponse, group: Group): EventModel =
            EventModel(
                eventResponse.id,
                group,
                EventTypes.fromInt(eventResponse.eventType),
                eventResponse.title,
                eventResponse.description,
                eventResponse.allDay,
                ZonedDateTime.parse(eventResponse.from, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .withZoneSameInstant(
                        ZoneId.systemDefault()
                    ).toLocalDateTime(),
                ZonedDateTime.parse(eventResponse.to, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .withZoneSameInstant(
                        ZoneId.systemDefault()
                    ).toLocalDateTime(),
                RepeatTypes.fromInt(eventResponse.repeatType),
                eventResponse.notifications.map { NotificationsTypes.fromInt(it) },
                eventResponse.attaches.map { Uri.parse(BuildConfig.ENDPOINT + it.path) },
                eventResponse.attaches.map { it.name }
            )
    }
}