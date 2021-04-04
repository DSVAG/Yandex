package com.dsvag.yandex.models.yandex.search

import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.ContentForAll,

    @Json(name = "query")
    val query: String = "query ContentForAll(\$searchText:String,\$sort:InstrumentListSort,\$ascending:Boolean,\$count:Int!,\$cursor:String){instruments{list(query:\$searchText,sort:\$sort,ascending:\$ascending,count:\$count,cursor:\$cursor){cursor results{... on AnyInstrumentItem{id slug type ticker displayName logoId taxFree commissionFree marketData{id accruedInterest price priceStep lotSize currencyCode percentChange:yearlyPercentChange absoluteChange:yearlyAbsoluteChange dailyPercentChange:percentChange dailyAbsoluteChange:absoluteChange bondYield{annualYield allTimeYield}}}}}}}",

    @Json(name = "variables")
    val variables: SearchVariables
)