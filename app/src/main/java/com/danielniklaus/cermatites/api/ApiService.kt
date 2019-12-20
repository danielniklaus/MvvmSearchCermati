package com.danielniklaus.cermatites.api

import androidx.lifecycle.LiveData
import com.danielniklaus.cermatites.data.SearchResponse
import com.danielniklaus.cermatites.usersearch.vo.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/{login}")
    fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>

    @GET("search/users")
    fun searchUser(@Query("q") query: String): LiveData<ApiResponse<SearchResponse>>

    @GET("search/users")
    fun searchUser(@Query("q") query: String, @Query("page") page: Int): Call<SearchResponse>
}