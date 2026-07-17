package infrastructure.persistence.converters

import androidx.room.TypeConverter
import infrastructure.persistence.types.Money

/**
 * Room converters for Money.
 * 
 * Database: INTEGER (amountMinor) + TEXT (currencyCode)
 * Application: Money value object
 * 
 * ADR-012: Prevent floating point precision loss in financial calculations.
 */
class MoneyConverters {

    @TypeConverter
    fun fromMoney(money: Money?): Long? = money?.amountMinor

    @TypeConverter
    fun toMoney(amountMinor: Long?): Money? {
        return amountMinor?.let { 
            Money(amountMinor = it, currencyCode = "SAR") // Default currency
        }
    }
}
