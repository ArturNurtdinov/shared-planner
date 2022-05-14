package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class UpdateEventBody(
    @SerializedName("only_update_instance")
    val onlyUpdateInstance: Boolean,
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
    @SerializedName("notifications")
    val notifications: List<Int>,
)