package com.journal.JournalAPI

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.LocalDate


object TimetableAPI {
    private var BASE_URL: String = ""
    private var token: String = ""

    data class TimetableData (
        @SerializedName("date")
        val date: String,
        @SerializedName("lesson")
        val lesson: String,
        @SerializedName("started_at")
        val started: String,
        @SerializedName("finished_at")
        val finished: String,
        @SerializedName("teacher_name")
        val nameTeacher: String,
        @SerializedName("subject_name")
        val nameSubject: String,
        @SerializedName("room_name")
        val nameRoom: String
    )

    private interface TimetableInter {
        @Headers(
            "Accept: application/json, text/plain, */*",
            "Accept-Language: ru_RU, ru",
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

        @GET("api/v2/schedule/operations/get-by-date-range")
        suspend fun getTimetable(
            @Query("date_start") startTime: LocalDate,
            @Query("date_end") endTime: LocalDate,
            @Header("Authorization") token: String
        ): List<TimetableData>
    }

    private val BuildTimetableApi: TimetableInter by lazy {
        Retrofit.Builder()
            .baseUrl(TimetableAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TimetableInter::class.java)
    }

    suspend fun getTimetable(baseurl: String,
                             token: String,
                             startTime: LocalDate,
                             endTime: LocalDate
    ): List<TimetableData> {
        BASE_URL = baseurl
        TimetableAPI.token = "Bearer " + token

        return BuildTimetableApi.getTimetable(startTime, endTime, TimetableAPI.token)
    }
}