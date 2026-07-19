package infrastructure.persistence.types

/**
 * Financial amount stored as minor units.
 *
 * Example:
 * 100.50 SAR = 10050 minor units
 *
 * ADR-012:
 * - Never use floating point for monetary values.
 * - Store smallest currency unit.
 * - Currency conversion belongs to application layer.
 */
data class Money(
    val amountMinor: Long,
    val currencyCode: String
) {

    companion object {

        fun fromMajor(
            amount: Double,
            currencyCode: String
        ): Money {

            return Money(
                amountMinor = (amount * 100).toLong(),
                currencyCode = currencyCode
            )
        }
    }


    fun toMajor(): Double {
        return amountMinor / 100.0
    }
}
