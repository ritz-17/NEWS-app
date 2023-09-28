package com.ritiesh.newsapp.repository

import com.ritiesh.newsapp.response.NewsResponse
import com.ritiesh.newsapp.webapi.ApiService
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getNews() = apiService.getNews()

    suspend fun getTopHeadlines(category: String, page: Int): Response<NewsResponse> {
        return apiService.getTopHeadlines(category = category, page = page)
    }

    suspend fun getSearchNews(query: String, page: Int): Response<NewsResponse> {
        return apiService.getSearchNews(searchQuery = query, page = page)
    }

    suspend fun getSourceNews() = apiService.getSourcesNews()


}