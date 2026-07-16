package infrastructure.persistence.entities

import infrastructure.persistence.model.SyncStatus


/**
 * Base persistence contract.
 *
 * Pure Kotlin abstraction.
 *
 * No Room dependency.
 *
 * Responsibilities:
 *
 * - Audit fields contract
 * - Soft delete contract
 * - Synchronization contract
 * - Optimistic locking contract
 */
abstract class BaseEntity(


    // ==========================
    // Identity
    // ==========================

    open val id: Long = 0,

    open val uuid: String? = null,


    // ==========================
    // Audit Trail
    // ==========================

    open val createdBy: Long,

    open val createdAt: String,


    open val updatedBy: Long? = null,

    open val updatedAt: String? = null,


    open val deletedBy: Long? = null,

    open val deletedAt: String? = null,


    // ==========================
    // Soft Delete
    // ==========================

    open val isDeleted: Int = 0,


    // ==========================
    // Synchronization
    // ==========================

    open val syncStatus: SyncStatus = SyncStatus.PENDING,

    open val syncVersion: Int = 1,

    open val syncAt: String? = null,


    open val deviceId: String? = null,


    // ==========================
    // Optimistic Locking
    // ==========================

    open val rowVersion: Int = 1,


    // ==========================
    // Metadata
    // ==========================

    open val remarks: String? = null,

    open val extraData: String? = null

)
