package com.dsvag.yandex.models.yandex.search

import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.ContentForAll,

    @Json(name = "query")
    val query: String = "query ContentForAll(\$searchText: String, \$sort: InstrumentListSort, \$ascending: Boolean, \$count: Int!, \$cursor: String) {\n  instruments {\n    list(query: \$searchText, sort: \$sort, ascending: \$ascending, count: \$count, cursor: \$cursor) {\n      cursor\n      results {\n        ... on AnyInstrumentItem {\n          id\n          slug\n          type\n          ticker\n          displayName\n          logoId\n          taxFree\n          commissionFree\n          marketData {\n            id\n            accruedInterest\n            price\n            priceStep\n            lotSize\n            currencyCode\n            percentChange: yearlyPercentChange\n            absoluteChange: yearlyAbsoluteChange\n            dailyPercentChange: percentChange\n            dailyAbsoluteChange: absoluteChange\n            bondYield {\n              annualYield\n              allTimeYield\n              }\n            }\n        }\n      }\n    }\n  }\n}\n",

    @Json(name = "variables")
    val variables: SearchVariables
)