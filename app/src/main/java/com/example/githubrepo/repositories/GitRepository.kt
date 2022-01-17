package com.example.githubrepo.repositories

import com.example.githubrepo.api.RetrofitInstance
import com.example.githubrepo.util.Constants.Companion.PERSONAL_ACCESS_TOKEN

class GitRepository {
    suspend fun getSearchRepositories(searchQuery: String, pageNumber: Int, perPageItem: Int) =
        RetrofitInstance.api.getRepositories(PERSONAL_ACCESS_TOKEN, searchQuery, pageNumber, perPageItem)

    suspend fun getContributors(owner: String, repo: String) =
        RetrofitInstance.api.getContributors(PERSONAL_ACCESS_TOKEN, owner, repo)

}