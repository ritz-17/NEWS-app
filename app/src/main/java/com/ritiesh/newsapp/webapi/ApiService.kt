package com.ritiesh.newsapp.webapi

import com.ritiesh.newsapp.response.NewsResponse
import com.ritiesh.newsapp.response.SourceResponse
import com.ritiesh.newsapp.utils.API_Key
import com.ritiesh.newsapp.utils.categories
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = "us",
        @Query("apiKey")
        apiKey: String = API_Key
    ): Response<NewsResponse>

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String = categories.first(),
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_Key,
    ): Response<NewsResponse>

    @GET("/v2/sources")
    suspend fun getSourcesNews(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = API_Key,
    ): Response<SourceResponse>

    @GET("v2/everything")
    suspend fun getSearchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_Key
    ): Response<NewsResponse>
}