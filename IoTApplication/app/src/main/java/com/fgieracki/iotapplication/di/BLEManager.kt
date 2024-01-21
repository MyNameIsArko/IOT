package com.fgieracki.iotapplication.di

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.util.Log
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

    private fun encryptMessage(message: String, key: String, iv: String): String {
        val encryptionManager = EncryptionManager();
        return encryptionManager.encrypt(message, key, iv)
    }

    fun getScannedDevices(): Flow<List<AndroidAdvertisement>> = scanner
        .advertisements
        .onStart {
            Log.i("BLEManager","Started searching")
        }
        .map {
            advertisements[it.address] = it
            advertisements.values.toList()
        }
        .onCompletion { cause ->
            Log.i("BLEManager","Stopped searching: $cause")
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
                    requestMtu(2000)
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
                Log.d("BLEManager","" + it.serviceUuid)
                Log.d("BLEManager","" + it.characteristicUuid)
                it.properties.read
            }.also {
                it.forEach {
                    Log.d("BLEManager", "Characteristics ${it.characteristicUuid}")
                    it.descriptors.forEach {
                        Log.d("BLEManager", "Descriptor ${it.descriptorUuid}")
                    }
                }
            }.getOrNull(0)
        } ?: throw IllegalArgumentException("No read characteristics found")


        val hardcodedKey = "1811035398261360";
        val hardcodedIV = "8197555279945598";

//        encryptMessage(ssid, hardcodedKey, hardcodedIV)

        val textToSend =
            "S{${encryptMessage(ssid, hardcodedKey, hardcodedIV)},"+
            "${encryptMessage(password, hardcodedKey, hardcodedIV)}," +
            "${userId}," +
            "${advertisement.address}," +
            "${token}," +
            "${aesKey}," +
            "${aesIV}}E"

        Log.d("BLEManager", "Sending: $textToSend")

        for(i in 0..textToSend.length step 20) {
            Log.d("BLEManager", "Sending: ")
            Log.d("BLEManager", textToSend.substring(i, (i + 20).coerceAtMost(textToSend.length)))
            // send message to characteristic async
            peripheral.write(
                characteristic = writeCharacteristics,
                textToSend.substring(i, (i + 20).coerceAtMost(textToSend.length)).toByteArray(),
            )
        }

        Log.i("BLEManager", "Write success, waiting for disconnect")

        val response =
            peripheral.read(writeCharacteristics).also {
                it.forEach {
                    Log.d("BLEManager", "$it")
                }
            }.decodeToString()

        Log.d("BLEManager","Use ${readCharacteristics.characteristicUuid}")
        Log.d("BLEManager","Response: $response")
        peripheral.disconnect()
    }


}