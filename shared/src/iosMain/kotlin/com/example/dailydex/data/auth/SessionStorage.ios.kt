package com.example.dailydex.data.auth

import platform.Foundation.NSUserDefaults

actual fun createSessionStorage(): SessionStorage =
    IosSessionStorage()

private class IosSessionStorage : SessionStorage {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun save(session: AuthSession) {

        defaults.setObject(session.accessToken, "access_token")
        defaults.setObject(session.refreshToken, "refresh_token")
        defaults.setObject(session.userId, "user_id")
        defaults.setObject(session.email, "email")
    }

    override fun load(): AuthSession? {

        val accessToken = defaults.stringForKey("access_token")
            ?: return null

        return AuthSession(
            accessToken = accessToken,
            refreshToken = defaults.stringForKey("refresh_token"),
            userId = defaults.stringForKey("user_id"),
            email = defaults.stringForKey("email")
        )
    }

    override fun clear() {

        listOf(
            "access_token",
            "refresh_token",
            "user_id",
            "email"
        ).forEach {

            defaults.removeObjectForKey(it)
        }
    }
}
