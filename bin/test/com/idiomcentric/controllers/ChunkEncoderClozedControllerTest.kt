package com.idiomcentric.controllers

import com.idiomcentric.IntegrationProvider
import com.idiomcentric.contollers.CreateChunk
import com.idiomcentric.contollers.CreateChunkEncoderClozed
import com.idiomcentric.dao.chunk.Chunk
import com.idiomcentric.dao.chunk.ChunkEncoderClozed
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChunkEncoderClozedControllerTest : IntegrationProvider() {

    @Inject
    @field:Client("/api/chunks")
    lateinit var chunkEncoderClozedClient: HttpClient

    @Test
    fun shouldReturnNoChunkEncoderClozedSuccessfully() {
        val chunkId = UUID.randomUUID()
        val actual: List<ChunkEncoderClozed> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ChunkEncoderClozed>>("/$chunkId/chunkencoderclozed"), Argument.listOf(ChunkEncoderClozed::class.java))

        Assertions.assertEquals(0, actual.size, "should return 0 chunkencoderclozed")
    }

    @Test
    fun shouldCreateChunk() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createChunkEncoderClozed = ArbCreateChunkEncoderClozed.next().copy(chunkId = chunk.id)

        val chunkEncoderClozed = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/chunkencoderclozed", createChunkEncoderClozed), ChunkEncoderClozed::class.java)

        Assertions.assertEquals(chunk.id, chunkEncoderClozed.chunkId, "chunk ids are the same")
        Assertions.assertEquals(createChunkEncoderClozed.sentence, chunkEncoderClozed.sentence, "sentence is the same")
        Assertions.assertEquals(createChunkEncoderClozed.clozedPositions, chunkEncoderClozed.clozedPositions, "clozedPositions are the same")
    }

    @Test
    fun shouldCreateDeleteChunk() {
        val createChunk = ArbCreateChunk.next()

        val chunk = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("", createChunk), Chunk::class.java)

        val createChunkEncoderClozed = ArbCreateChunkEncoderClozed.next().copy(chunkId = chunk.id)

        val chunkEncoderClozed = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/${chunk.id}/chunkencoderclozed", createChunkEncoderClozed), ChunkEncoderClozed::class.java)

        val actual: List<ChunkEncoderClozed> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ChunkEncoderClozed>>("/${chunk.id}/chunkencoderclozed"), Argument.listOf(ChunkEncoderClozed::class.java))

        Assertions.assertEquals(1, actual.size, "should return 1 chunkencoderclozed")

        val deleteRequest = HttpRequest.DELETE<Nothing>("/${chunk.id}/chunkencoderclozed/${chunkEncoderClozed.id}")

        chunkEncoderClozedClient
            .toBlocking()
            .exchange<Nothing, Nothing>(deleteRequest)

        val actualAfterDelete: List<ChunkEncoderClozed> = chunkEncoderClozedClient
            .toBlocking()
            .retrieve(HttpRequest.GET<List<ChunkEncoderClozed>>("/${chunk.id}/chunkencoderclozed"), Argument.listOf(ChunkEncoderClozed::class.java))

        Assertions.assertEquals(0, actualAfterDelete.size, "should return 0 chunkencoderclozed")
    }
}

val ArbCreateChunkEncoderClozed = arbitrary {
    val chunkId = Arb.uuid().bind()
    val sentence = Arb.string(10..12).bind()
    val clozedPosition = Arb.int()
    val clozedPositions = Arb.list(clozedPosition).bind()
    CreateChunkEncoderClozed(
        chunkId = chunkId,
        sentence = sentence,
        clozedPositions = clozedPositions
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
