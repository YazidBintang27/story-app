package com.latihan.storyou.data.remote.service

import com.latihan.storyou.data.remote.models.DetailStoryResponse
import com.latihan.storyou.data.remote.models.LoginResponse
import com.latihan.storyou.data.remote.models.PostResponse
import com.latihan.storyou.data.remote.models.RegisterResponse
import com.latihan.storyou.data.remote.models.StoriesResponse
import com.latihan.storyou.utils.ApiConstant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
   @FormUrlEncoded
   @POST(ApiConstant.REGISTER_ENDPOINT)
   suspend fun register(
      @Field("name") name: String,
      @Field("email") email: String,
      @Field("password") password: String
   ): RegisterResponse

   @FormUrlEncoded
   @POST(ApiConstant.LOGIN_ENDPOINT)
   suspend fun login(
      @Field("email") email: String,
      @Field("password") password: String
   ): LoginResponse

   @Multipart
   @POST(ApiConstant.ADD_STORIES_ENDPOINT)
   suspend fun postStories(
      @Header("Authorization") token: String,
      @Part("description") description: RequestBody,
      @Part photo: MultipartBody.Part,
      @Part("lat") lat: RequestBody? = null,
      @Part("lon") lon: RequestBody? = null
   ): PostResponse

   @Multipart
   @POST(ApiConstant.ADD_STORIES_GUEST_ENDPOINT)
   suspend fun postStoriesGuest(
      @Part("description") description: RequestBody,
      @Part photo: MultipartBody.Part,
      @Part("lat") lat: RequestBody? = null,
      @Part("lon") lon: RequestBody? = null
   ): PostResponse

   @GET(ApiConstant.GET_ALL_STORIES_ENDPOINT)
   suspend fun getAllStories(
      @Header("Authorization") token: String
   ): StoriesResponse

   @GET(ApiConstant.DETAIL_STORIES_ENDPOINT)
   suspend fun getDetailStories(
      @Header("Authorization") token: String,
      @Path("id") id: String
   ): DetailStoryResponse
}