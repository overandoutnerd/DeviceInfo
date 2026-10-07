package com.overandoutnerd.deviceinfo.home

data class UserDeviceDetailsProperty(
    val key: String,
    val value: String
)

data class ComponentDetailsState(
    var loading: Boolean = true,
    var componentDetails: List<UserDeviceDetailsProperty?> = emptyList(),
    var error: String? = null
)
