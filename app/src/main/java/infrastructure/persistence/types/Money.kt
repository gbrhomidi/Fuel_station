package infrastructure.persistence.types

/**
 * Financial amount stored as minor units (smallest currency unit).
 * 
 * Example: 100.50 SAR = 10050 minor units
 * 
 * ADR-012: Financial Precision Contract
 * - Never use floating point (REAL) for monetary values
 * - Always store in smallest unit (Integer/Long)
 * - Currency conversion handled at application layer
 */
data class Money(
    val amountMinor: Long,
    val currencyCode: String
) {
    companion object {
        fun fromMajor(amount: Double, currencyCode: String): Money {
            return Money(
                amountMinor = (amount * 100).toLong(),
                currencyCode = currencyCode
            )
        }
    }
    
    fun toMajor(): Double = amountMinor / 100.0
}
