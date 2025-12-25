package com.journal

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import com.journal.JournalAPI.JournalAPI
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class UpdateTimeManager {
    var monday: LocalDate
    val sunday: LocalDate
    val mondayNext: LocalDate
    val sundayNext: LocalDate
    private var passWeek = StorageMMKV.kv.decodeBool("PassWeek")

    init {
        val currentTimetable = StorageMMKV.getTimetable("CurrentTimetable")

        val lastLessonDate = currentTimetable?.lastOrNull()?.day
        val firstLessonDate = currentTimetable?.firstOrNull()?.day
        val thisMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        monday = when {
            passWeek -> {
                thisMonday.plusWeeks(1)
            }

            lastLessonDate != null && LocalDate.now().isAfter(lastLessonDate) -> {
                StorageMMKV.kv.remove("PassWeek")
                StorageMMKV.kv.encode("PassWeek", true)
                passWeek = true
                thisMonday.plusWeeks(1)
            }

            firstLessonDate != null && LocalDate.now().isAfter(firstLessonDate) -> {
                StorageMMKV.kv.remove("PassWeek")
                StorageMMKV.kv.encode("PassWeek", false)
                passWeek = false
                thisMonday
            }

            else -> {
                thisMonday
            }
        }

        sunday = monday.plusDays(6)
        mondayNext = monday.plusWeeks(1)
        sundayNext = mondayNext.plusDays(6)

    }

    suspend fun updateTimetable() {
        val currentTimetableOld = StorageMMKV.getTimetable("CurrentTimetable")
        val nextTimetableOld = StorageMMKV.getTimetable("NextTimetable")

        if (currentTimetableOld != null && nextTimetableOld != null) {
            val api = JournalAPI()
            try {
                val currentTimetableNew = api.getListTimetable(monday, sunday)
                val nextTimetableNew = api.getListTimetable(mondayNext, sundayNext)

                StorageMMKV.comparisonTimetable(currentTimetableOld, currentTimetableNew, "CurrentTimetable")
                StorageMMKV.comparisonTimetable(nextTimetableOld, nextTimetableNew, "NextTimetable")

                Log.d("Update", "Successful")
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }

        } else {
            val api = JournalAPI()

            val currentTimetable = api.getListTimetable(monday, sunday)
            val nextTimetable = api.getListTimetable(mondayNext, sundayNext)

            StorageMMKV.saveTimetable("CurrentTimetable", currentTimetable)
            StorageMMKV.saveTimetable("NextTimetable", nextTimetable)

            Log.d("Error", "Timetable = null")

        }

    }

    suspend fun TestLogin(
        login: TextFieldState,
        password: TextFieldState,
        error: (String) -> Unit,
        onDismiss: () -> Unit){
        try {
            StorageMMKV.kv.encode("User", login.text.toString())
            StorageMMKV.kv.encode("Password", password.text.toString())

            updateTimetable()

            StorageMMKV.kv.encode("isFirstRun", false)
            onDismiss()
        } catch (e: Exception) {
            StorageMMKV.kv.remove("User")
            StorageMMKV.kv.remove("Password")
            error("Error, login or password uncorrect.")
        }
    }
}