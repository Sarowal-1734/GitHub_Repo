package com.example.githubrepo.api

import com.example.githubrepo.models.RepositoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {
    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q")
        q: String?,
        @Query("page")
        page: Int?,
        @Query("per_page")
        per_page: Int?
    ): Response<RepositoryResponse>

//    @GET("repos/")
//    suspend fun getContributor(
//        @Query("query")
//        query: String = "Android",
//        @Query("page")
//        page: Int = 1,
//        @Query("per_page")
//        per_page: Int = 20
//    )

}