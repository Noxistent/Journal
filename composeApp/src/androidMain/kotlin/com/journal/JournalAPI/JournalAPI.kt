package com.journal.JournalAPI

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.journal.StorageMMKV
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Lesson(
    val num: Short,
    val startedAt: LocalTime,
    val finishedAt: LocalTime,
    val teacherName: String,
    val subjectName: String,
    val roomName: String
) : Parcelable

@Parcelize
data class Timetable (
    val day: LocalDate,
    val listLessons: List<Lesson>
) : Parcelable

class JournalAPI (
    private val user: String = StorageMMKV.getUser(),
    private val password: String = StorageMMKV.getPassword(),
    private val BASE_URL: String = "https://msapi.top-academy.ru/",
) : ViewModel() {

    public suspend fun getAuthData(): AuthenticationAPI.AuthData {
        return AuthenticationAPI.getAuthKEY(BASE_URL, user, password)
    }

    public suspend fun getListTimetable(
        startTime: LocalDate,
        endTime: LocalDate
    ): MutableList<Timetable> {
        Log.d("startTime =", startTime.toString())
        Log.d("endTime =", endTime.toString())

        val listDirtyTimetable = TimetableAPI.getTimetable(
            BASE_URL,
            getAuthData().accessToken,
            startTime,
            endTime)

        val listClearTimetable = sortTimetable(listDirtyTimetable)

        listClearTimetable.sortBy { it.day }

        return listClearTimetable

    }

    private fun sortTimetable(list: List<TimetableAPI.TimetableData>): MutableList<Timetable> {
        val listTimetable: MutableList<Timetable> = mutableListOf()

        for (day in list) {
            if (boolListTable(day, listTimetable)){
                continue
            }

            val listLessons: MutableList<Lesson> = mutableListOf()

            for (otherDay in list) {
                if (day.date == otherDay.date){
                    val lesson = Lesson(
                        otherDay.lesson.toShort(),
                        sortTime(otherDay.started),
                        sortTime(otherDay.finished),
                        otherDay.nameTeacher,
                        otherDay.nameSubject,
                        otherDay.nameRoom
                    )
                    listLessons.add(lesson)
                }
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            listTimetable.add(
                Timetable(
                LocalDate.parse(day.date, formatter),
                listLessons
                )
            )
        }
        return listTimetable
    }

    private fun boolListTable(day: TimetableAPI.TimetableData, list: MutableList<Timetable>): Boolean {
        for (time in list){
            if (day.date == time.day.toString()){
                return true
            }
        }
        return false
    }

    private fun sortTime(timeStr: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(timeStr, formatter)
    }

}