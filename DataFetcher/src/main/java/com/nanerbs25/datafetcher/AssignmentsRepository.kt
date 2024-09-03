package com.nanerbs25.datafetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AssignmentsRepository {

    val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.57:3000/") // Replace with your actual backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun fetchAssignments(start: Int, pageSize: Int): List<Assignment> {
        return withContext(Dispatchers.IO) {
            val assignmentDtos = apiService.getAssignments(start, pageSize)
            assignmentDtos.map { it.toAssignment() }
        }
    }


    suspend fun uploadAssignment(title: String, description: String, imageFile: File) {
        return withContext(Dispatchers.IO) {
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
            val response = apiService.uploadAssignment(title, description, imagePart)
            response.isSuccessful
        }
    }
}
