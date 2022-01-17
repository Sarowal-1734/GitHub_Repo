package com.example.githubrepo.api

import com.example.githubrepo.models.ContributorResponse
import com.example.githubrepo.models.RepositoryResponse
import com.example.githubrepo.util.Constants.Companion.PERSONAL_ACCESS_TOKEN
import retrofit2.Response
import retrofit2.http.*

interface GitHubApi {
    @GET("search/repositories")
    suspend fun getRepositories(
        @Header("Authorization") accessToken: String,
        @Query("q")
        q: String?,
        @Query("page")
        page: Int?,
        @Query("per_page")
        per_page: Int?
    ): Response<RepositoryResponse>

    @GET("/repos/{owner}/{repo}/stats/contributors")
    suspend fun getContributors(
        @Header("Authorization") accessToken: String,
        @Path("owner")
        owner: String?,
        @Path("repo")
        repo: String?
    ): Response<ContributorResponse>

}