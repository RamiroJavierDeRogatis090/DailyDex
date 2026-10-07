package com.example.dailydex.data.auth

import android.content.Context
import com.example.dailydex.AppContext

actual fun createSessionStorage(): SessionStorage =
    AndroidSessionStorage(AppContext.applicationContext)

private class AndroidSessionStorage(
    context: Context
) : SessionStorage {

    private val prefs = context.getSharedPreferences(
        "dailydex_auth",
        Context.MODE_PRIVATE
    )

    override fun save(session: AuthSession) {

        prefs.edit().apply {

            putString("access_token", session.accessToken)
            putString("refresh_token", session.refreshToken)
            putString("user_id", session.userId)
            putString("email", session.email)

        }.apply()
    }

    override fun load(): AuthSession? {

        val accessToken = prefs.getString("access_token", null)
            ?: return null

        return AuthSession(
            accessToken = accessToken,
            refreshToken = prefs.getString("refresh_token", null),
            userId = prefs.getString("user_id", null),
            email = prefs.getString("email", null)
        )
    }

    override fun clear() {

        prefs.edit().clear().apply()
    }
}
