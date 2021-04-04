package com.dsvag.yandex.models.yandex.chart


import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChartRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.Chart,
    @Json(name = "query")
    val query: String = "query Chart(\$count: Int!, \$to: IsoDateTime, \$instrumentId: ID!, \$candleSize: CandleSize!, \$excludeTs: Boolean!, \$exchange: Exchange) {\n  candles(exchange: \$exchange, instrumentId: \$instrumentId) {\n    earliestTs @skip(if: \$excludeTs)\n    seriesBefore(to: \$to, count: \$count, candleSize: \$candleSize)\n  }\n}\n",
    @Json(name = "variables")
    val variables: ChartVariables
)