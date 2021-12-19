package com.idiomcentric.service

import io.micronaut.core.annotation.Introspected

@Introspected
data class RedditResponse(val data: RedditData)

@Introspected
data class RedditData(
    val children: List<RedditChild>
)

@Introspected
data class RedditChild(
    val data: RedditChildData
)

@Introspected
data class RedditChildData(
    val title: String,
    val url: String,
    val isVideo: Boolean,
)