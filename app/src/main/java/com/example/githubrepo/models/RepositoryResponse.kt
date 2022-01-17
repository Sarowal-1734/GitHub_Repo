package com.example.githubrepo.models

data class RepositoryResponse(
    val items: MutableList<Item>,
    val total_count: Int
)