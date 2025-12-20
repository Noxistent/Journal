package com.journal

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object UpdateTimeManager {
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
}