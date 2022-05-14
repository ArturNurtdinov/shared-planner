package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("ID")
    val id: String,
    @SerializedName("GroupID")
    val groupId: Long,
    @SerializedName("EventType")
    val eventType: Int,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("AllDay")
    val allDay: Boolean,
    @SerializedName("From")
    val from: String,
    @SerializedName("To")
    val to: String,
    @SerializedName("RepeatType")
    val repeatType: Int,
    @SerializedName("Notifications")
    val notifications: List<Int>,
    @SerializedName("Attachments")
    val attaches: List<String>,
)