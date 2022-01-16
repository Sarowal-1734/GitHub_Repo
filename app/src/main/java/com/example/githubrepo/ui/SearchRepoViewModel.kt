package com.example.githubrepo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.githubrepo.RepoApplication
import com.example.githubrepo.models.RepositoryResponse
import com.example.githubrepo.repositories.GitRepository
import com.example.githubrepo.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.githubrepo.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchRepoViewModel(
    val app: Application,
    private val gitRepository: GitRepository
) : AndroidViewModel(app) {

    val repositories: MutableLiveData<Resource<RepositoryResponse>> = MutableLiveData()
    var repositoriesPageNumber = 1
    var repositoriesPageResponse: RepositoryResponse? = null
    // Show/Hide paginationProgressBar
    private val isLoading = MutableLiveData<Boolean>()

    fun getRepositories(searchQuery: String) =
        viewModelScope.launch {
            safeGetRepositoriesCall(searchQuery)
        }

    private suspend fun safeGetRepositoriesCall(
        searchQuery: String
    ) {
        if (repositoriesPageNumber == 1) {
            repositories.postValue(Resource.Loading())
        } else {
            isLoading.value = true
        }

        try {
            if (hasInternetConnection()) {
                val response =
                    gitRepository.getSearchRepositories(
                        searchQuery,
                        repositoriesPageNumber,
                        QUERY_PAGE_SIZE
                    )
                repositories.postValue(handleSearchRepoResponse(response))
            } else {
                repositories.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> repositories.postValue(Resource.Error("Network Failure"))
                else -> repositories.postValue(Resource.Error("Conversion error"))
            }
        }
        isLoading.value = false
    }

    private fun handleSearchRepoResponse(response: Response<RepositoryResponse>): Resource<RepositoryResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // Pagination
                repositoriesPageNumber++
                if (repositoriesPageResponse == null) {
                    repositoriesPageResponse = resultResponse
                } else {
                    val oldRepo = repositoriesPageResponse?.items
                    val newRepo = resultResponse.items
                    oldRepo?.addAll(newRepo)
                }
                repositoriesPageResponse
                return Resource.Success(repositoriesPageResponse ?: resultResponse)
            }
        }
        return if (response.message().isEmpty()) {
            Resource.Error("Request failed. Please try again later")
        } else {
            Resource.Error(response.message())
        }
    }


    // Check internet connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<RepoApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    // Show/Hide progressBar
    fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

}