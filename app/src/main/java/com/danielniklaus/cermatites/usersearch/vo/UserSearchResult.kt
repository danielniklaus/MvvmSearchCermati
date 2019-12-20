package com.danielniklaus.cermatites.usersearch.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.danielniklaus.cermatites.data.AppTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(AppTypeConverters::class)
data class UserSearchResult(
    val query: String,
    val userIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)