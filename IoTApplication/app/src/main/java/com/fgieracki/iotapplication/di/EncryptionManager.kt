package com.fgieracki.iotapplication.di

import android.util.Log
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionManager {
    val algorithm = "AES/CBC/PKCS5Padding"


    // ref: https://www.baeldung.com/java-aes-encryption-decryption
    fun decrypt(cipherTextInput: String, keyValue: String, ivValue: String): String {
        try {
            val cipherText = cipherTextInput.replace("\n", "")
            Log.d("EncryptionManager", "cipherText: $cipherText, keyValue: $keyValue, ivValue: $ivValue")
            val msgBytes = Base64.getDecoder().decode(cipherText)

            val key = SecretKeySpec(keyValue.toByteArray(), "AES")
            val iv = IvParameterSpec(ivValue.toByteArray())
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            val plainText = cipher.doFinal(msgBytes)
            return String(plainText)
        } catch (ex: IllegalBlockSizeException) {
        Log.e("Encryption Manager", "${ex.message}")
        } catch (ex: BadPaddingException) {
            Log.e("Encryption Manager", "${ex.message}")
        } catch (ex: InvalidKeyException) {
            Log.e("Encryption Manager", "${ex.message}")
        } catch (ex: NoSuchAlgorithmException) {
            Log.e("Encryption Manager", "${ex.message}")
        } catch (ex: NoSuchPaddingException) {
            Log.e("Encryption Manager", "${ex.message}")
        } catch (ex: Exception) {
            Log.e("Encryption Manager", "${ex.message}")
        }
        return ""
    }

    fun encrypt(inputText: String, keyValue: String, ivValue: String): String {
        val key = SecretKeySpec(keyValue.toByteArray(), "AES")
        val iv = IvParameterSpec(ivValue.toByteArray())
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    fun generateKey(length: Int = 15): String {
        val allowedChars = ('0'..'9')
        var key = "5"
        repeat(length) {
            key += allowedChars.random()
        }
        return key
    }

    fun generateIV(length: Int = 15): String {
        val allowedChars = ('0'..'9')
        var key = "5"
        repeat(length) {
            key += allowedChars.random()
        }
        return key
    }
}