package com.fgieracki.iotapplication.di

class TokenGenerator {
     fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var result = ""
        repeat(length) {
            result += allowedChars.random()
        }
        return result
    }
}