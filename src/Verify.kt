package org.ovo.mockserver

import com.google.gson.annotations.SerializedName

data class Verify(
    val refId: String,
    val email: String,
    val deviceId: String,
    val verificationCode: String,
    val mobile: String,
    val newEmail: String,
    val pushNotificationId: String,
    val appVersion: String,
    val osName: String,
    val macAddress: String,
    val osVersion: String)
