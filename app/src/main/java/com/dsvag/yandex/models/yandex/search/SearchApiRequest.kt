package com.dsvag.yandex.models.yandex.search

import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchApiRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.ContentForAll,

    @Json(name = "query")
    val query: String = "query ContentForAll(\$searchText:String,\$sort:InstrumentListSort,\$ascending:Boolean,\$count:Int!,\$cursor:String){instruments{list(query:\$searchText,sort:\$sort,ascending:\$ascending,count:\$count,cursor:\$cursor){cursor,results{...on AnyInstrumentItem{ticker,displayName,logoId,marketData{price,currencyCode,percentChange,absoluteChange}}}}}}",

    @Json(name = "variables")
    val variables: SearchVariables
)