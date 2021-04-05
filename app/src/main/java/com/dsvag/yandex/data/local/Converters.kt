package com.dsvag.yandex.data.local

import androidx.room.TypeConverter
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromLong(value: Long): BigDecimal = BigDecimal(value).divide(BigDecimal(1000))

    @TypeConverter
    fun toLong(bigDecimal: BigDecimal): Long = bigDecimal.multiply(BigDecimal(1000)).toLong()
}