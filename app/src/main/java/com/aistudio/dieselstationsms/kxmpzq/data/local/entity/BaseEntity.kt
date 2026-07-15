package com.aistudio.dieselstationsms.kxmpzq.data.local.entity

import java.time.Instant

abstract class BaseEntity(
    open val id: String,
    open val createdAt: Instant,
    open val updatedAt: Instant,
    open val version: Long
)
