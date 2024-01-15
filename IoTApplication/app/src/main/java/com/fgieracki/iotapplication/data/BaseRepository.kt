package com.fgieracki.iotapplication.data

import android.util.Log
import com.fgieracki.iotapplication.domain.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("safeApiCall", "Calling api: $apiToBeCalled")
                val response: Response<T> = apiToBeCalled()
                Log.i("safeApiCall", response.toString())
                if (response.isSuccessful) {
                    Resource.Success(data = response.body()!!)
                } else {
                    val jObjError = JSONObject(
                        response.errorBody()!!.string()
                    )
                    Log.e("SafeApiCall", "Error: ${jObjError.getString("message")}")

                    Resource.Error(errorMessage = jObjError.getString("message"), code = response.code())
                }
            } catch (e: HttpException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong", code = e.code())
            } catch (e: IOException) {
                Log.e("safeApiCall", "IOException: ${e.message}")
                Resource.Error("Please check your network connection", code = 500)
            } catch (e: Exception) {
                Log.e("safeApiCall", "Exception: ${e.message}")
                Resource.Error(errorMessage = "Something went wrong.", code = 500)
            }
        }
    }
}