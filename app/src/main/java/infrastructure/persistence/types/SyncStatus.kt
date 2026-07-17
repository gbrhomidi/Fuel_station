package infrastructure.persistence.types

/**
 * Synchronization state persisted in SQLite as INTEGER.
 * ADR-011: Offline Optimistic Synchronization Contract.
 *
 * Storage mapping:
 * PENDING  = 0
 * SYNCING  = 1
 * SYNCED   = 2
 * FAILED   = 3
 *
 * Never store String values in database.
 */
enum class SyncStatus(val code: Int) {
    PENDING(0),
    SYNCING(1),
    SYNCED(2),
    FAILED(3);

    companion object {
        fun fromCode(code: Int): SyncStatus {
            return entries.firstOrNull { it.code == code } ?: FAILED
        }
    }
}
