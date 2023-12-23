package com.fgieracki.iotapplication.di

class TokenGenerator {
    public fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString { "" }
    }
}