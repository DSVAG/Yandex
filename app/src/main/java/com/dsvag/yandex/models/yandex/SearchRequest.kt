package com.dsvag.yandex.models.yandex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchRequest(
    @Json(name = "operationName")
    val operationName: String = "ContentForAll",

    @Json(name = "query")
    val query: String= "query ContentForAll(\$searchText:String,\$sort:InstrumentListSort,\$ascending:Boolean,\$count:Int!,\$cursor:String){instruments{list(query:\$searchText,sort:\$sort,ascending:\$ascending,count:\$count,cursor:\$cursor){cursor,results{...on AnyInstrumentItem{ticker,displayName,logoId,marketData{price,currencyCode,percentChange,absoluteChange}}}}}}",

    @Json(name = "variables")
    val variables: Variables
)