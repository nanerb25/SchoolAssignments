package com.nanerbs25.datafetcher


import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("assignments")
    suspend fun getAssignments(
        @Query("start") start: Int,
        @Query("end") end: Int
    ): List<AssignmentDto>

    @GET("uploads/{imageName}")
    suspend fun getImage(@Path("imageName") imageName: String): ResponseBody

    @Multipart
    @POST("/assignments")
    suspend fun uploadAssignment(
        @Part("title") title: String,
        @Part("description") description: String,
        @Part image: MultipartBody.Part
    ): retrofit2.Response<Void>
}
