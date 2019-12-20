package com.danielniklaus.cermatites.data

import com.danielniklaus.cermatites.usersearch.vo.User
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<User>
) {
    var nextPage: Int? = null
}