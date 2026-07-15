package com.aistudio.dieselstationsms.kxmpzq.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(
    tableName = "roles",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["code"], unique = true)
    ]
)
data class RoleEntity(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val description: String? = null,
    val isActive: Boolean = true,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val version: Long = 0
) : BaseEntity(
    id = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
    version = version
)
