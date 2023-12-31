package com.ritiesh.newsapp.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ritiesh.newsapp.repository.NewsRepository
import com.ritiesh.newsapp.viewmodel.NewsViewModel

@Suppress("UNCHECKED_CAST")
class NewsViewModelFactory(
    private val application: Application,
    private val repository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(application,repository) as T
    }
}