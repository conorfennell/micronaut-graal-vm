package com.idiomcentric.service

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Singleton
import kotlinx.coroutines.FlowPreview
import java.net.URI

@Singleton
class RedditLowLevelClient(
    @param:Client(RedditConfiguration.REDDIT_API_URL)
    private val httpClient: HttpClient,
    configuration: RedditConfiguration
) {

    private val uri: URI = UriBuilder.of(configuration.subReddit)
        .path(configuration.query)
        .queryParam("limit", configuration.limit)
        .queryParam("t", configuration.t)
        .build()

    @OptIn(FlowPreview::class)
    fun fetchTopPosts(): List<RedditChildData> {
        val request = HttpRequest.GET<RedditResponse>(uri)

        return httpClient.toBlocking()
            .retrieve(request, RedditResponse::class.java).data.children.map { it.data }

    }
}