package org.ovo.mockserver

data class SecurityCode(var email: String? = null,
                        var mobile: String? = null,
                        var securityCode: String? = null,
                        var updateAccessToken: String? = null,
                        var mPushNotificationId: String? = null,
                        var mDeviceId: String? = null,
                        val osName: String = "android",
                        val osVersion: String = "",
                        val appVersion: String = "",
                        val macAddress: String = "") {
    private val deviceUnixtime: Long = System.currentTimeMillis() / 1000
}