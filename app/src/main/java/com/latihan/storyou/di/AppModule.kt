package com.latihan.storyou.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.latihan.storyou.data.local.datastore.AuthPreferences
import com.latihan.storyou.data.local.room.StoryDao
import com.latihan.storyou.data.local.room.StoryDatabase
import com.latihan.storyou.data.remote.service.ApiService
import com.latihan.storyou.utils.ApiConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

   @Provides
   @Singleton
   fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
      return ChuckerInterceptor.Builder(context).build()
   }

   @Provides
   @Singleton
   fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient {
      return OkHttpClient.Builder()
         .addInterceptor(chuckerInterceptor)
         .build()
   }

   @Provides
   @Singleton
   fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
      return Retrofit.Builder()
         .baseUrl(ApiConstant.BASE_URL)
         .client(okHttpClient)
         .addConverterFactory(GsonConverterFactory.create())
         .build()
   }

   @Provides
   @Singleton
   fun provideService(retrofit: Retrofit): ApiService {
      return retrofit.create(ApiService::class.java)
   }

   @Provides
   @Singleton
   fun provideAuthPreferences(@ApplicationContext context: Context): AuthPreferences {
      return AuthPreferences(context)
   }

   @Provides
   @Singleton
   fun providesDatabase(@ApplicationContext context: Context): StoryDatabase {
      return Room.databaseBuilder(
         context,
         StoryDatabase::class.java,
         "story_database"
      ).build()
   }

   @Provides
   @Singleton
   fun providesDao(database: StoryDatabase): StoryDao {
      return database.storyDao()
   }
}