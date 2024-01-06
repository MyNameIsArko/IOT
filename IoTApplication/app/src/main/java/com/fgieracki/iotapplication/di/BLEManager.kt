package com.fgieracki.iotapplication.di

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.juul.kable.AndroidAdvertisement
import com.juul.kable.Filter
import com.juul.kable.Scanner
import com.juul.kable.peripheral
import com.juul.kable.read
import com.juul.kable.write
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.lang.Thread.sleep

class BLEManager {
    val scanner = Scanner {
        filters = listOf( // SensorTag
            Filter.NamePrefix("ESP"),
        )
    }

    private val bluetoothManager by lazy {
        ContextCatcher.getContext().getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val advertisements = hashMapOf<String, AndroidAdvertisement>()

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCatcher.getContext().startActivity(enableBtIntent)
        }
    }

    fun isBluetoothEnabled(): Boolean {
        if(bluetoothAdapter == null) return false
        return bluetoothAdapter!!.isEnabled
    }

    fun getScannedDevices(): Flow<List<AndroidAdvertisement>> = scanner
        .advertisements
        .onStart {
            println("Started searching")
        }
        .map {
            advertisements[it.address] = it
            advertisements.values.toList()
        }
        .onCompletion { cause ->
            println("Stopped searching: $cause")
        }

    suspend fun sendMessageToDevice(
        advertisement: AndroidAdvertisement,
        token: String,
        aesKey: String,
        aesIV: String,
        userId: String,
        ssid: String,
        password: String,
    ) {
        val peripheral = scope
            .peripheral(advertisement = advertisement) {
                onServicesDiscovered {
                    requestMtu(998)
                }
            }

        peripheral.connect()
        sleep(1500)

        val writeCharacteristics = peripheral.services?.firstNotNullOfOrNull {
            it.characteristics.firstOrNull {
                it.properties.write
            }
        } ?: throw IllegalArgumentException("No write characteristics found")

        val readCharacteristics = peripheral.services?.firstNotNullOfOrNull {
            it.characteristics.filter {
                println(it.serviceUuid)
                println(it.characteristicUuid)
                it.properties.read
            }.also {
                it.forEach {
                    println("Characteristics ${it.characteristicUuid}")
                    it.descriptors.forEach {
                        println("Descriptor ${it.descriptorUuid}")
                    }
                }
            }.getOrNull(0)
        } ?: throw IllegalArgumentException("No read characteristics found")

        val textToSend =
            "START{\"ssid\":\"$ssid\"," +
            "\"password\":\"$password\"," +
            "\"user_id\":\"$userId\"," +
            "\"mac\":\"${advertisement.address}\"," +
            "\"token\":\"${token}\"," +
            "\"aes_key\":\"${aesKey}\"," +
            "\"aes_iv\":\"${aesIV}\"}END"

        println("Sending: $textToSend")

        for(i in 0..textToSend.length step 20) {
            print("Sending: ")
            println(textToSend.substring(i, (i + 20).coerceAtMost(textToSend.length)))
            peripheral.write(
                characteristic = writeCharacteristics,
                textToSend.substring(i, (i + 20).coerceAtMost(textToSend.length)).toByteArray(),
            )
            sleep(100)
        }

        println("Write success, waiting for disconnect")

        val response =
            peripheral.read(readCharacteristics).also {
                it.forEach {
                    print(it)
                    print(" ")
                }
            }.decodeToString().also(::println)

        println("Wybrałem ${readCharacteristics.characteristicUuid}")

        println("Response: $response")
        /*if (response != "OK") {
            throw BluetoothException.Other("Not ok")
        }*/
        peripheral.disconnect()
    }


}