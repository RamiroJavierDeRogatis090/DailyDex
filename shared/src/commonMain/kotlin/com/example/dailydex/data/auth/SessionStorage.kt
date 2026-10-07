package com.example.dailydex.data.auth

interface SessionStorage {
    fun save(session: AuthSession)
    fun load(): AuthSession?
    fun clear()
}

expect fun createSessionStorage(): SessionStorage
