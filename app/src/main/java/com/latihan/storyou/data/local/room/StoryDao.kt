package com.latihan.storyou.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertStories(stories: List<StoryEntity>)

   @Query("SELECT * FROM story_table ORDER BY createdAt DESC")
   fun getStories(): PagingSource<Int, StoryEntity>

   @Query("DELETE FROM story_table")
   suspend fun deleteAll()
}