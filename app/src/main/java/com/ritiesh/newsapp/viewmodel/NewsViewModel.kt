package com.ritiesh.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ritiesh.newsapp.app.NewsApp
import com.ritiesh.newsapp.repository.NewsRepository
import com.ritiesh.newsapp.response.NewsResponse
import com.ritiesh.newsapp.response.SourceResponse
import com.ritiesh.newsapp.storage.UIModeDataStore
import com.ritiesh.newsapp.utils.Resource
import com.ritiesh.newsapp.utils.categories
import com.ritiesh.newsapp.utils.hasInternetConnection
import com.ritiesh.newsapp.utils.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    application: Application,
    private val repository: NewsRepository
) : AndroidViewModel(application) {

    private val uiModeDataStore = UIModeDataStore(application)

    // get ui mode
    val getUIMode = uiModeDataStore.uiMode

    // save ui mode
    fun saveToDataStore(isNightMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            uiModeDataStore.saveToDataStore(isNightMode)
        }
    }

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    val newsSourcesData: MutableLiveData<Resource<SourceResponse>> = MutableLiveData()

    private val newsDataTemp = MutableLiveData<Resource<NewsResponse>>()

    private var news = 1
    private var headlinenews = 1
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getNews()
    }

    fun getNews() = viewModelScope.launch {
        fetchNews()
    }

    fun getHeadlinesNews(category: String = categories.first()) = viewModelScope.launch {
        fetchheadlinews(category)
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        fetchsearchnews(searchQuery)
    }

    fun getSourcesNews() = viewModelScope.launch {
        fetchSourcesNews()
    }

    private suspend fun fetchNews() {
        newsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getNews()
                newsDataTemp.postValue(Resource.Success(response.body()!!))
                newsData.postValue(handleNewsResponse(response))
            } else {
                newsData.postValue(Resource.Error("No Internet Connection"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private suspend fun fetchheadlinews(category: String = categories.first()) {
        newsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getTopHeadlines(category, headlinenews)
                newsData.postValue(handleNewsResponse(response))
            } else {
                newsData.postValue(Resource.Error("No Internet Connection"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private suspend fun fetchsearchnews(searchQuery: String) {
        newsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getSearchNews(searchQuery, searchNewsPage)
                newsData.postValue(handleSearchNewsResponse(response))
            } else {
                newsData.postValue(Resource.Error("No Internet Connection"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private suspend fun fetchSourcesNews() {
        newsSourcesData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getSourceNews()
                newsSourcesData.postValue(handleSourceNewsResponse(response))
            } else {
                newsSourcesData.postValue(Resource.Error("No Internet Connection"))
                toast(getApplication(), "No Internet Connection.!")
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsSourcesData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsSourcesData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private fun handleSourceNewsResponse(response: Response<SourceResponse>): Resource<SourceResponse>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    fun onSearchClose() {
        newsData.postValue(newsDataTemp.value)
    }
}