package com.example.githubrepo.repositories

import com.example.githubrepo.api.RetrofitInstance

class GitRepository {
    suspend fun getSearchRepositories(searchQuery: String, pageNumber: Int, perPageItem: Int) =
        RetrofitInstance.api.getRepositories(searchQuery, pageNumber, perPageItem)
}