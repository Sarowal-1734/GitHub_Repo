package com.example.githubrepo.models

data class RepositoryResponse(
    //val incomplete_results: Boolean,
    val items: MutableList<Item>,
    val total_count: Int
)