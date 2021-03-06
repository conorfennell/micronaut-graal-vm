package com.idiomcentric

import com.idiomcentric.service.reddit.RedditPost
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.ReadTimeoutException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.matchers.Times
import org.mockserver.model.HttpError
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedditControllerTest : IntegrationProvider() {

    override fun getProperties(): MutableMap<String, String> {
        val properties = super.getProperties() + mapOf(
            "reddit.host" to "http://${mockServer.host}",
            "reddit.port" to mockServer.serverPort.toString(),
            "micronaut.caches.headlines.expire-after-write" to "1ms"
        )

        return properties.toMutableMap()
    }

    @Inject
    @field:Client("/reddit")
    lateinit var redditClient: HttpClient

    @Test
    fun shouldReturnRedditResponseSuccessfully() {
        // GET r/worldnews/top.json?limit=10&t=day
        mockServerClient
            .`when`(
                request()
                    .withPath("/r/worldnews/top.json")
                    .withQueryStringParameter("limit", "10")
                    .withQueryStringParameter("t", "day")
            )
            .respond(
                response()
                    .withBody(loadResource("200_r_worldnews_top_json_ten.json"), MediaType.APPLICATION_JSON)
            )

        val actual: List<RedditPost> = redditClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<RedditPost>>("/top"), Argument.listOf(RedditPost::class.java))

        Assertions.assertEquals(3, actual.size, "should return three posts")
    }

    @Test
    fun shouldThrowReadTimeoutException() {
        mockServerClient
            .`when`(
                request()
                    .withPath("/r/worldnews/top.json")
                    .withQueryStringParameter("limit", "10")
                    .withQueryStringParameter("t", "day"),
                Times.exactly(1)
            )
            .error(
                HttpError
                    .error()
                    .withDropConnection(true)
            )

        val thrown = Assertions.assertThrows(
            ReadTimeoutException::class.java,
            {
                redditClient
                    .toBlocking()
                    .retrieve(HttpRequest.GET<List<RedditPost>>("/top"), Argument.listOf(RedditPost::class.java))
            },
            "ReadTimeoutException was expected"
        )

        Assertions.assertEquals("Read Timeout", thrown.message)
    }
}
