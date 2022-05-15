package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class OnlyDeleteInstanceBody(
    @SerializedName("only_delete_instance")
    val onlyDeleteInstance: Boolean
)