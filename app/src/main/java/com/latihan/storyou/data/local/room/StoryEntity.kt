package com.latihan.storyou.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_table")
data class StoryEntity (
   @PrimaryKey(autoGenerate = false)
   val id: String,
   val createdAt: String,
   val description: String,
   val name: String,
   val photoUrl: String,
   val lat: Double?,
   val lon: Double?
)