package com.example.dailydex.data.auth

object AuthManager {

    private val storage: SessionStorage by lazy {
        createSessionStorage()
    }

    private var cached: AuthSession? = null

    val accessToken: String?
        get() = cached?.accessToken

    val userId: String?
        get() = cached?.userId

    val isAuthenticated: Boolean
        get() = cached?.isUsable == true

    fun restore(): AuthSession? {

        cached = storage.load()

        return cached
    }

    fun set(session: AuthSession) {

        cached = session

        storage.save(session)
    }

    fun clear() {

        cached = null

        storage.clear()
    }
}
