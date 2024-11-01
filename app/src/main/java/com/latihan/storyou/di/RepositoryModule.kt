package com.latihan.storyou.di

import com.latihan.storyou.data.repository.Repository
import com.latihan.storyou.data.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
   @Binds
   @Singleton
   abstract fun bindRepository(repositoryImpl: RepositoryImpl): Repository
}