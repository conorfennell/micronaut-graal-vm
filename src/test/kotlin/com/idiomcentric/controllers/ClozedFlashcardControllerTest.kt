package com.idiomcentric.controllers

import com.idiomcentric.IntegrationProvider
import com.idiomcentric.contollers.CreateChunk
import com.idiomcentric.contollers.CreateClozedFlashcard
import com.idiomcentric.dao.chunk.Chunk
import com.idiomcentric.dao.chunk.ClozedFlashcard
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Duration
import java.time.Instant
import java.util.UUID

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClozedFlashcardControllerTest : IntegrationProvider() {

    @Inject
    @field:Client("/api/chunks")
    lateinit var chunkEncoderClozedClient: HttpClient

    @Test
    fun shouldReturnNoChunkEncoderClozedSuccessfully() {
        val chunkId = UUID.randomUUID()
        val actual: List<ClozedFlashcard> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ClozedFlashcard>>("/$chunkId/flashcards/clozed"), Argument.listOf(ClozedFlashcard::class.java))

        Assertions.assertEquals(0, actual.size, "should return 0 flashcards")
    }

    @Test
    fun shouldReturnNoClozedFlashcardSuccessfully() {
        val chunkId = UUID.randomUUID()
        val clozedFlashcardId = UUID.randomUUID()
        val thrown = Assertions.assertThrows(
            HttpClientResponseException::class.java,
            {
                chunkEncoderClozedClient
                    .toBlocking()
                    .retrieve(HttpRequest.GET<ClozedFlashcard>("/$chunkId/flashcards/clozed/$clozedFlashcardId"), ClozedFlashcard::class.java)
            },
            "HttpClientResponseException Not Found expected"
        )

        Assertions.assertEquals("Not Found", thrown.message)
    }

    @Test
    fun shouldReturnNoClozedFlashcardDeletedSuccessfully() {
        val chunkId = UUID.randomUUID()
        val clozedFlashcardId = UUID.randomUUID()
        val thrown = Assertions.assertThrows(
            HttpClientResponseException::class.java,
            {
                val deleteRequest = HttpRequest.DELETE<Nothing>("/$chunkId/flashcards/clozed/$clozedFlashcardId")

                chunkEncoderClozedClient
                    .toBlocking()
                    .exchange<Nothing, Nothing>(deleteRequest)
            },
            "HttpClientResponseException Not Found expected"
        )

        Assertions.assertEquals("Not Found", thrown.message)
    }

    @Test
    fun shouldRetrieveClozedFlashcard() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createChunkEncoderClozed = ArbCreateClozedFlashcard.next().copy(chunkId = chunk.id)

        val clozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/flashcards/clozed", createChunkEncoderClozed), ClozedFlashcard::class.java)

        val getClozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<ClozedFlashcard>("/${chunk.id}/flashcards/clozed/${clozedFlashcard.id}"), ClozedFlashcard::class.java)

        Assertions.assertEquals(clozedFlashcard, getClozedFlashcard, "clozed flashcard should be the same")
    }

    @Test
    fun shouldCreateClozedFlashcard() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createChunkEncoderClozed = ArbCreateClozedFlashcard.next().copy(chunkId = chunk.id)

        val clozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/flashcards/clozed", createChunkEncoderClozed), ClozedFlashcard::class.java)

        Assertions.assertEquals(chunk.id, clozedFlashcard.chunkId, "chunk ids are the same")
        Assertions.assertEquals(createChunkEncoderClozed.sentence, clozedFlashcard.sentence, "sentence is the same")
        Assertions.assertEquals(createChunkEncoderClozed.clozedPositions, clozedFlashcard.clozedPositions, "clozedPositions are the same")
    }

    @Test
    fun shouldCreateDeleteChunk() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createChunkEncoderClozed = ArbCreateClozedFlashcard.next().copy(chunkId = chunk.id)

        val clozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/flashcards/clozed", createChunkEncoderClozed), ClozedFlashcard::class.java)

        val actual: List<ClozedFlashcard> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ClozedFlashcard>>("/${chunk.id}/flashcards/clozed"), Argument.listOf(ClozedFlashcard::class.java))

        Assertions.assertEquals(1, actual.size, "should return 1 flashcards")

        val deleteRequest = HttpRequest.DELETE<Nothing>("/${chunk.id}/flashcards/clozed/${clozedFlashcard.id}")

        chunkEncoderClozedClient
            .toBlocking()
            .exchange<Nothing, Nothing>(deleteRequest)

        val actualAfterDelete: List<ClozedFlashcard> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ClozedFlashcard>>("/${chunk.id}/flashcards/clozed"), Argument.listOf(ClozedFlashcard::class.java))

        Assertions.assertEquals(0, actualAfterDelete.size, "should return 0 clozed flashcard")
    }

    @Test
    fun shouldCreateUpdateChunk() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createClozedFlashcard = ArbCreateClozedFlashcard.next().copy(chunkId = chunk.id)

        val clozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/flashcards/clozed", createClozedFlashcard), ClozedFlashcard::class.java)

        val updatedSentence = "UPDATED"
        chunkEncoderClozedClient
            .toBlocking()
            .exchange<ClozedFlashcard, Nothing>(HttpRequest.PUT("/${chunk.id}/flashcards/clozed/${clozedFlashcard.id}", clozedFlashcard.copy(sentence = updatedSentence)))

        val actual: ClozedFlashcard = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ClozedFlashcard>>("/${chunk.id}/flashcards/clozed/${clozedFlashcard.id}"), ClozedFlashcard::class.java)

        Assertions.assertEquals(updatedSentence, actual.sentence, "should return 1 flashcards")
    }

    @Test
    fun shouldUpdateClozedFlashcard() {
        val chunkId = UUID.randomUUID()
        val clozedFlashcard = ArbClozedFlashcard.next()

        val thrown = Assertions.assertThrows(
            HttpClientResponseException::class.java,
            {
                chunkEncoderClozedClient
                    .toBlocking()
                    .exchange<ClozedFlashcard, Nothing>(HttpRequest.PUT("/$chunkId/flashcards/clozed/${clozedFlashcard.id}", clozedFlashcard))
            },
            "HttpClientResponseException Not Found expected"
        )

        Assertions.assertEquals("Not Found", thrown.message)
    }
}

val ArbCreateClozedFlashcard = arbitrary {
    val chunkId = Arb.uuid().bind()
    val sentence = Arb.string(10..12).bind()
    val clozedPosition = Arb.int()
    val clozedPositions = Arb.list(clozedPosition).bind()
    CreateClozedFlashcard(
        chunkId = chunkId,
        sentence = sentence,
        clozedPositions = clozedPositions
    )
}

val ArbClozedFlashcard = arbitrary {
    val id = Arb.uuid().bind()
    val chunkId = Arb.uuid().bind()
    val sentence = Arb.string(10..12).bind()
    val clozedPosition = Arb.int()
    val clozedPositions = Arb.list(clozedPosition).bind()
    val updatedAt = Arb.instant(Instant.now(), Instant.now().plusSeconds(Duration.ofDays(365).toSeconds())).bind()
    val createdAt = Arb.instant(Instant.now(), Instant.now().plusSeconds(Duration.ofDays(365).toSeconds())).bind()
    ClozedFlashcard(
        id = id,
        chunkId = chunkId,
        sentence = sentence,
        clozedPositions = clozedPositions,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

val ArbCreateChunk = arbitrary {
    val title = Arb.string(10..12).bind()
    val body = Arb.string(10..12).bind()
    CreateChunk(
        title = title,
        body = body,
    )
}
