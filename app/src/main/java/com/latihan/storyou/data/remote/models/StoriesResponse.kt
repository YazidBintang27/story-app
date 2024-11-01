package com.latihan.storyou.data.remote.models


import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("listStory")
    val listStory: List<Story?>?,
    @SerializedName("message")
    val message: String?
) {
    data class Story(
        @SerializedName("createdAt")
        val createdAt: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("lat")
        val lat: Double?,
        @SerializedName("lon")
        val lon: Double?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("photoUrl")
        val photoUrl: String?
    )
}