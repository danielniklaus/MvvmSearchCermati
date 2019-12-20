package com.danielniklaus.cermatites.usersearch.vo

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
abstract class UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: UserSearchResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUser(repositories: List<User>)

    @Query("SELECT * FROM UserSearchResult WHERE `query` = :query")
    abstract fun search(query: String): LiveData<UserSearchResult>

    @Query("SELECT * FROM user WHERE login = :login")
    abstract fun findByLogin(login: String): LiveData<User>

    fun loadOrdered(userIds: List<Int>): LiveData<List<User>> {
        val order = SparseIntArray()
        userIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(loadById(userIds)) { user ->
            Collections.sort(user) { r1, r2 ->
                val pos1 = order.get(r1.id)
                val pos2 = order.get(r2.id)
                pos1 - pos2
            }
            user
        }
    }

    @Query("SELECT * FROM User WHERE id in (:userIds)")
    protected abstract fun loadById(userIds: List<Int>): LiveData<List<User>>

    @Query("SELECT * FROM UserSearchResult WHERE `query` = :query")
    abstract fun findSearchResult(query: String): UserSearchResult?
}
