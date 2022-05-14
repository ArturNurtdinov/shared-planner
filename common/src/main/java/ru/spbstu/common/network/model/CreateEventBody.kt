package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes

data class CreateEventBody(
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("event_type")
    val eventType: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("all_day")
    val allDay: Boolean,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("repeat_type")
    val repeatType: Int,
    @SerializedName("notifications")
    val notificationsType: List<Int>,
    @SerializedName("attachments")
    val attachments: List<String>,
)