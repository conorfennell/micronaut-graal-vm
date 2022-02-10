package com.idiomcentric.dao.chunk

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.UUID

object ChunkTable : Table("CHUNK") {
    val id = uuid("ID")
    val title = varchar("TITLE", length = 255)
    var text = text("TEXT") // TODO what should collate be set to?
    val createdAt = timestamp("CREATED_AT")
    val updatedAt = timestamp("UPDATED_AT")

    override val primaryKey = PrimaryKey(id)
}

data class Chunk(
    val id: UUID,
    val title: String,
    val text: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)