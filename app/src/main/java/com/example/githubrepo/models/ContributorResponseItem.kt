package com.example.githubrepo.models

data class ContributorResponseItem(
    val author: Author,
    val total: Int,
    val weeks: List<Week>
)