package com.example.githubrepo.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubrepo.repositories.GitRepository

class SearchRepoViewModelProviderFactory(
    val app: Application,
    val gitRepository: GitRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchRepoViewModel(app, gitRepository) as T
    }
}