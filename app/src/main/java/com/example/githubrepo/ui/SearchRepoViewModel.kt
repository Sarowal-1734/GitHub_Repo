package com.example.githubrepo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
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

    // get search repository
    fun getRepositories(searchQuery: String) =
        viewModelScope.launch {
            safeGetRepositoriesCall(searchQuery)
        }

    // get search repository
    private suspend fun safeGetRepositoriesCall(
        searchQuery: String
    ) {
        if (repositoriesPageNumber == 1) {
            repositories.postValue(Resource.Loading())
        } else {
            repositories.postValue(Resource.Paginating())
        }

        try {
            if (hasInternetConnection()) {
                // get search repository response
                val response =
                    gitRepository.getSearchRepositories(
                        searchQuery,
                        repositoriesPageNumber,
                        QUERY_PAGE_SIZE
                    )
                // // handle the search repository response
                repositories.postValue(handleSearchRepoResponse(response))
            } else {
                repositories.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> repositories.postValue(Resource.Error("Network Failure"))
                else -> repositories.postValue(Resource.Error("Conversion error. Please try again later"))
            }
        }
    }

    private suspend fun handleSearchRepoResponse(response: Response<RepositoryResponse>): Resource<RepositoryResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // get most active contributor
                getContributor(resultResponse)
                // Pagination
                repositoriesPageNumber++
                if (repositoriesPageResponse == null) {
                    repositoriesPageResponse = resultResponse
                } else {
                    val oldRepo = repositoriesPageResponse?.items
                    val newRepo = resultResponse.items
                    oldRepo?.addAll(newRepo)
                }
                return Resource.Success(repositoriesPageResponse ?: resultResponse)
            }
        }
        return if (response.message().isEmpty()) {
            Resource.Error("Request failed. Please try again later")
        } else {
            Resource.Error(response.message())
        }
    }

    // get most active contributor
    private suspend fun getContributor(resultResponse: RepositoryResponse) {
        var i = 0
        while (i != resultResponse.items.size) {
            // will get a list of contributors for every repo
            val contributorResponse = resultResponse.items[i].name.let {
                gitRepository.getContributors(
                    resultResponse.items[i].owner.login,
                    it
                )
            }
            if (contributorResponse.isSuccessful && contributorResponse.body() != null) {
                val contributors = contributorResponse.body()
                var totalAdditions = 0
                var totalDeletions = 0
                var totalCommits = 0
                var maxCount = 0
                var bestContributorsName = ""

                if (contributors != null) {
                    // iterate every contributor
                    var j = 0
                    while (j != contributors.size) {
                        var additions = 0
                        var deletions = 0
                        var commits = 0
                        // iterate every week
                        var k = 0
                        while (k != contributors[j].weeks.size) {
                            // calculate week data for every single contributor
                            additions += contributors[j].weeks[k].a
                            deletions += contributors[j].weeks[k].d
                            commits += contributors[j].weeks[k].c
                            k++
                        }
                        val totalCount = commits + additions + deletions
                        if (totalCount > maxCount) {
                            maxCount = totalCount
                            totalAdditions = additions
                            totalDeletions = deletions
                            totalCommits = commits
                            bestContributorsName = contributors[j].author.login
                        }
                        j++
                    }
                }
                resultResponse.items[i].best_contributor = bestContributorsName
                resultResponse.items[i].additions = totalAdditions
                resultResponse.items[i].deletions = totalDeletions
                resultResponse.items[i].commits = totalCommits
            }   // else gitHub limit exceed
            i++
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

}