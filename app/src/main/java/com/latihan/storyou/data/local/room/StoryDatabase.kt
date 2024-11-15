package com.latihan.storyou.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
   abstract fun storyDao(): StoryDao
   abstract fun remoteKeysDao(): RemoteKeysDao
}