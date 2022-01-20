package com.example.githubrepo.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepo.adapters.RepositoryAdapter
import com.example.githubrepo.databinding.FragmentSearchRepoBinding
import com.example.githubrepo.ui.MainActivity
import com.example.githubrepo.ui.SearchRepoViewModel
import com.example.githubrepo.util.Constants
import com.example.githubrepo.util.Resource

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchRepoBinding

    private lateinit var viewModel: SearchRepoViewModel
    private lateinit var repoAdapter: RepositoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchRepoBinding.inflate(inflater, container, false)

        viewModel = (activity as MainActivity).viewModel

        // Setup recyclerView
        setupRecyclerView()

        // on search icon clicked to search repo
        binding.imageViewSearchButton.setOnClickListener {
            val searchQuery = binding.editTextSearchRepo.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                closeKeyboard()
                viewModel.repositoriesPageResponse = null
                viewModel.repositoriesPageNumber = 1
                viewModel.getRepositories(searchQuery)
            } else {
                binding.editTextSearchRepo.error = "Required"
            }
        }

        // hide keyboard and search for repo on search icon clicked
        binding.editTextSearchRepo.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    binding.imageViewSearchButton.performClick()
                    true
                }
                else -> false
            }
        }

        // on recycler item click to open repo in browser
        repoAdapter.setOnItemClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.html_url))
            startActivity(intent)
        }

        viewModel.repositories.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { repoResponse ->
                        repoAdapter.differ.submitList(repoResponse.items.toList())
                        // Pagination
                        val totalPages = repoResponse.total_count / Constants.QUERY_PAGE_SIZE
                        isLastPage = viewModel.repositoriesPageNumber == totalPages
                        if (isLastPage) {
                            binding.recyclerViewRepo.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showMainProgressBar()
                }
                is Resource.Paginating -> {
                    showPaginationProgressBar()
                }
            }
        })

        return binding.root
    }

    // hide progressBar
    private fun hideProgressBar() {
        binding.initialProgressBar.visibility = View.GONE
        binding.paginationProgressBar.visibility = View.GONE
    }

    // show initial progressBar
    private fun showMainProgressBar() {
        binding.initialProgressBar.visibility = View.VISIBLE
    }

    // show pagination progressBar
    private fun showPaginationProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    // Pagination
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLeadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLeadingAndNotLastPage && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                val searchQuery = binding.editTextSearchRepo.text.toString().trim()
                if (searchQuery.isNotEmpty()) {
                    viewModel.getRepositories(searchQuery)
                    isScrolling = false
                } else {
                    binding.editTextSearchRepo.error = "Required"
                }
            }
        }
    }

    // Setup recyclerView
    private fun setupRecyclerView() {
        repoAdapter = RepositoryAdapter()
        binding.recyclerViewRepo.apply {
            adapter = repoAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    // Hide keyboard
    private fun closeKeyboard() {
        val manager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}