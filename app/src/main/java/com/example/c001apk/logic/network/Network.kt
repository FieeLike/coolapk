package com.example.c001apk.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {

    private val apiService = ServiceCreator.create<ApiService>()
    private val searchService = SearchServiceCreator.create<ApiService>()

    suspend fun getHomeFeed(page: Int, firstLaunch: Int, installTime: String, lastItem: String) =
        apiService.getHomeFeed(page, firstLaunch, installTime, lastItem).await()

    suspend fun getFeedContent(id: String) =
        apiService.getFeedContent(id).await()

    suspend fun getFeedContentReply(id: String, discussMode: Int, listType: String, page: Int) =
        apiService.getFeedContentReply(id, discussMode, listType, page).await()

    suspend fun getSearchFeed(
        type: String,
        feedType: String,
        sort: String,
        keyWord: String,
        page: Int
    ) = searchService.getSearchFeed(type, feedType, sort, keyWord, page).await()

    suspend fun getSearchUser(
        keyWord: String,
        page: Int
    ) = searchService.getSearchUser(keyWord, page).await()

    suspend fun getSearchTopic(
        type: String,
        keyWord: String,
        page: Int
    ) = searchService.getSearchTopic(type, keyWord, page).await()

    suspend fun getReply2Reply(
        id: String,
        page: Int
    ) = searchService.getReply2Reply(id, page).await()

    suspend fun getHomeTopicTitle() = searchService.getHomeTopicTitle().await()

    suspend fun getTopicLayout(tag: String) = apiService.getTopicLayout(tag).await()

    suspend fun getTopicData(url: String, title: String, subTitle: String?, page: Int) =
        apiService.getTopicData(url, title, subTitle, page).await()

    suspend fun getHomeRanking(page: Int, lastItem: String) =
        searchService.getHomeRanking(page, lastItem).await()

    suspend fun getUserSpace(uid: String) =
        searchService.getUserSpace(uid).await()

    suspend fun getUserFeed(uid: String, page: Int) =
        searchService.getUserFeed(uid, page).await()

    suspend fun getAppInfo(id: String) =
        searchService.getAppInfo(id).await()

    suspend fun getAppComment(url: String, page: Int) =
        searchService.getAppComment(url, page).await()

    suspend fun getProfile(uid: String) =
        apiService.getProfile(uid).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}