package com.dsvag.yandex.models.yandex.stockPrice


import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockPriceRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.InstrumentHot,

    @Json(name = "query")
    val query: String = "query InstrumentHot(\$slug:String!,\$hasBroker:Boolean!,\$hasIIS:Boolean!){instruments{metaData:bySlug(slug:\$slug){... on AnyInstrumentItem{id marketData{id price bidPrice askPrice timestamp absoluteChange percentChange currencyCode}}}}...PortfolioBalanceFragment profile{hasHistory:hasHistoryBySlug(slug:\$slug)}}fragment PortfolioBalanceFragment on Query{brokerPortfolio:portfolio(agreementType:BROKER)@include(if:\$hasBroker){main{currency{id amount instrument{id slug assetCurrencyCode}}}position:positionBySlug(slug:\$slug){... on AnyPortfolioPosition{id amount}}}iisPortfolio:portfolio(agreementType:IIS)@include(if:\$hasIIS){main{currency{id amount instrument{id slug assetCurrencyCode}}}position:positionBySlug(slug:\$slug){... on AnyPortfolioPosition{id amount}}}}",

    @Json(name = "variables")
    val stockPriceVariables: StockPriceVariables
)