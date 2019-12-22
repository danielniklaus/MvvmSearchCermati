package com.danielniklaus.cermatites.di

import android.app.Application
import androidx.room.Room
import com.danielniklaus.cermatites.api.ApiService
import com.danielniklaus.cermatites.data.AppDb
import com.danielniklaus.cermatites.usersearch.vo.UserDao
import com.danielniklaus.cermatites.util.Constants
import com.danielniklaus.cermatites.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class  AppModule {
    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Timber.i(it)
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDb {
        return Room
            .databaseBuilder(app, AppDb::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDb): UserDao {
        return db.userDao()
    }

}
