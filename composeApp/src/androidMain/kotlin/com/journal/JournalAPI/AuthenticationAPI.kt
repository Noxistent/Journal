package com.journal.JournalAPI

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object AuthenticationAPI {
    private var BASE_URL: String = ""
    data class AuthData (
        @SerializedName("access_token")
        val accessToken: String
    )

    data class LoginData(
        @SerializedName("application_key")
        val appKey: String =  "6a56a5df2667e65aab73ce76d1dd737f7d1faef9c52e8b8c55ac75f565d8e8a6",

        @SerializedName("id_city")
        val city: String = "null",

        @SerializedName("username")
        val user: String,

        @SerializedName("password")
        val password: String
    )

    private interface AuthApi {
        @Headers(
            "Accept: application/json, text/plain, */*",
            "Accept-Language: ru_RU, ru",
            "Authorization: Bearer null",
            "Connection: keep-alive",
            "Origin: https://journal.top-academy.ru",
            "Referer: https://journal.top-academy.ru/",
            "Sec-Fetch-Dest: empty",
            "Sec-Fetch-Mode: cors",
            "Sec-Fetch-Site: same-site",
            "Sec-GPC: 1",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:145.0) Gecko/20100101 Firefox/145.0",
            "content-type: application/json"
        )
        @POST("api/v2/auth/login")
        suspend fun getAuthKEY(@Body data: LoginData): AuthData
    }

    private val BuildAuthApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    suspend fun getAuthKEY(baseurl: String, user: String, password: String): AuthData {
        BASE_URL = baseurl
        return BuildAuthApi.getAuthKEY(
            LoginData(user = user, password = password)
        )
    }

}