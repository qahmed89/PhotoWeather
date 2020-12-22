package com.example.photoweather.di

import android.content.Context
import androidx.room.Room

import com.example.photoweather.db.WeatherDatabase
import com.example.photoweather.other.Constants.BASE_URL
import com.example.photoweather.other.Constants.DATABASE_NAME
import com.example.photoweather.remote.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providerRoomDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
            app,
            WeatherDatabase::class.java,
            DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: WeatherDatabase) = db.getWeatherDao()


    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
            @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun proviedMovieAPI(): WeatherApi {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(WeatherApi::class.java)
    }
}