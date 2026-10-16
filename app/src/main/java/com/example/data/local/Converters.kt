package com.example.data.local

import androidx.room.TypeConverter

class StringListConverters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString("|||")
    }
}
