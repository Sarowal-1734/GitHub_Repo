package com.example.githubrepo.models

data class Item(
    val id: Int,
    val name: String,
    val owner: Owner,
    val description: String,
    val language: String,
    val updated_at: String,

    val contributors_url: String,
    val html_url: String
)