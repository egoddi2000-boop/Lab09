package com.example.lab09

import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {

    @GET("users")
    suspend fun getUsers(): List<UserModel>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserModel
}