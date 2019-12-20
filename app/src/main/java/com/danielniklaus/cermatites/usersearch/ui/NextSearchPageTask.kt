package com.danielniklaus.cermatites.usersearch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.danielniklaus.cermatites.api.*
import com.danielniklaus.cermatites.data.AppDb
import com.danielniklaus.cermatites.data.Resource
import com.danielniklaus.cermatites.usersearch.vo.UserSearchResult
import java.io.IOException

class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: ApiService,
    private val db: AppDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val current = db.userDao().findSearchResult(query)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response = githubService.searchUser(query, nextPage).execute()
            val apiResponse = ApiResponse.create(response)
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    // we merge all repo ids into 1 list so that it is easier to fetch the
                    // result list.
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.userIds)

                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = UserSearchResult(
                        query, ids,
                        apiResponse.body.total, apiResponse.nextPage
                    )
                    db.runInTransaction {
                        db.userDao().insert(merged)
                        db.userDao().insertUser(apiResponse.body.items)
                    }
                    Resource.success(apiResponse.nextPage != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
