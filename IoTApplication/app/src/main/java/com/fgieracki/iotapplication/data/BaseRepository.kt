package com.fgieracki.iotapplication.data

import com.fgieracki.iotapplication.data.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()

                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    Resource.Error(errorMessage = "Something went wrong: ${response.message()}", code = response.code())
                }
            } catch (e: HttpException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong", code = e.code())
            } catch (e: IOException) {
                Resource.Error("Please check your network connection", code = 500)
            } catch (e: Exception) {
                Resource.Error(errorMessage = "Something went wrong", code = 500)
            }
        }
    }

//    suspend fun login(username: String, password: String): Response<LoginResponse>
//    suspend fun register(username: String, password: String): Response<StringResponse>
//    fun getDevices(): Flow<List<Device>>
//    suspend fun addDevice(device: Device): Boolean
//    suspend fun deleteDevice(device: Device): Boolean
//    suspend fun updateDevice(device: Device): Boolean
}