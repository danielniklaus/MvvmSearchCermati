package com.danielniklaus.cermatites.usersearch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.danielniklaus.cermatites.AppExecutors
import com.danielniklaus.cermatites.api.ApiService
import com.danielniklaus.cermatites.api.ApiSuccessResponse
import com.danielniklaus.cermatites.data.AppDb
import com.danielniklaus.cermatites.data.Resource
import com.danielniklaus.cermatites.data.SearchResponse
import com.danielniklaus.cermatites.usersearch.vo.User
import com.danielniklaus.cermatites.usersearch.vo.UserDao
import com.danielniklaus.cermatites.usersearch.vo.UserSearchResult
import com.danielniklaus.cermatites.util.LiveDataCheck
import com.danielniklaus.cermatites.util.NetworkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val db:AppDb,
    private val githubService: ApiService
) {

    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }

            override fun shouldFetch(data: User?) = data == null

            override fun loadFromDb() = userDao.findByLogin(login)

            override fun createCall() = githubService.getUser(login)
        }.asLiveData()
    }
    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            githubService = githubService,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }
    fun search(query: String): LiveData<Resource<List<User>>> {
        return object : NetworkBoundResource<List<User>, SearchResponse>(appExecutors) {

            override fun saveCallResult(item: SearchResponse) {
                val userIds = item.items.map { it.id }
                val userSearchResult = UserSearchResult(
                    query = query,
                    userIds = userIds,
                    totalCount = item.total,
                    next = item.nextPage
                )
                db.runInTransaction {
                    userDao.insertUser(item.items)
                    userDao.insert(userSearchResult)
                }
            }

            override fun shouldFetch(data: List<User>?) = data == null

            override fun loadFromDb(): LiveData<List<User>> {
                return Transformations.switchMap(userDao.search(query)) { searchData ->
                    if (searchData == null) {
                        LiveDataCheck.create()
                    } else {
                        userDao.loadOrdered(searchData.userIds)
                    }
                }
            }

            override fun createCall() = githubService.searchUser(query)

            override fun processResponse(response: ApiSuccessResponse<SearchResponse>)
                    : SearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }
}
