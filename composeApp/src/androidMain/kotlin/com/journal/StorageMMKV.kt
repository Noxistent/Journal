package com.journal

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.journal.JournalAPI.Timetable
import com.tencent.mmkv.MMKV
import kotlinx.parcelize.Parcelize

object StorageMMKV {
    public val kv: MMKV by lazy {
        MMKV.defaultMMKV()
    }

    public fun init(context: Context) {
        MMKV.initialize(context)
    }

    public fun getUser(): String {
        val user = kv.decodeString("User")

        if (user != null) {
            return user
        } else {
            Log.d("Error", "USERNAME = null")
            return ""
        }

    }

    public fun getPassword(): String {
        val password = kv.decodeString("Password")

        if (password != null) {
            return password
        } else {
            Log.d("Error", "USERNAME = null")
            return ""
        }
    }

    @Parcelize
    private class PaddingClassOverMutableList(
        val timetable: MutableList<Timetable>) : Parcelable


    public fun saveTimetable(strName: String, timetable: MutableList<Timetable>) {
        kv.encode(strName, PaddingClassOverMutableList(timetable))
    }

    public fun comparisonTimetable(timetableOld: MutableList<Timetable>, timetableNew: MutableList<Timetable>, strName: String) {
        if (timetableOld != timetableNew) {
            StorageMMKV.kv.remove(strName)
            StorageMMKV.saveTimetable(strName, timetableNew)
        }
    }

    public fun getTimetable(strName: String): MutableList<Timetable>? {
        val paddingClass = kv.decodeParcelable(strName, PaddingClassOverMutableList::class.java)
        return paddingClass?.timetable
    }
}