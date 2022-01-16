package com.example.githubrepo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.githubrepo.R
import com.example.githubrepo.repositories.GitRepository

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SearchRepoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gitRepository = GitRepository()
        val viewModelProviderFactory = SearchRepoViewModelProviderFactory(application, gitRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(SearchRepoViewModel::class.java)

    }
}