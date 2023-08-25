package com.kabu.myapplication.repository

import com.kabu.myapplication.api.RemoteDataSource
import com.kabu.myapplication.model.CreateResponse
import com.kabu.myapplication.model.CreateTicketRequest
import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.model.TokenResponse
import com.kabu.myapplication.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class Repository constructor(private val source: RemoteDataSource) : BaseApiResponse() {

    suspend fun login(
        username: String = "",
        password: String = ""
    ): Flow<NetworkResult<TokenResponse>> {
        return flow {
            emit(safeApiCall {
                source.onLogin(username = username, password = password)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun userInfo(): Flow<NetworkResult<UserInfoResponse>> {
        return flow<NetworkResult<UserInfoResponse>> {
            emit(safeApiCall {
                source.userInfo()
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTicketList(id: String): Flow<NetworkResult<List<TicketResponse>>> {
        return flow<NetworkResult<List<TicketResponse>>> {
            emit(safeApiCall {
                source.getListTicket(id)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun onCreateTicket(request: CreateTicketRequest): Flow<NetworkResult<CreateResponse>> {
        return flow<NetworkResult<CreateResponse>> {
            emit(safeApiCall {
                source.createTicketRequest(request)
            })
        }.flowOn(Dispatchers.IO)
    }

}

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null, code: Int? = 400) :
        NetworkResult<T>(data, message, code)

    class Loading<T> : NetworkResult<T>()
}

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        var code = 0;
        try {
            val response = apiCall()
            code = response.code();
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            } else {
                return NetworkResult.Error(message = response.message(), code = response.code())
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString(), code)
        }
    }

    private fun <T> error(errorMessage: String, code: Int?): NetworkResult<T> =
        NetworkResult.Error(message = "Api call failed $errorMessage", code = code)
}
