package com.dsvag.yandex.models.yandex.stockPrice


import com.dsvag.yandex.models.yandex.Operation
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockPriceRequest(
    @Json(name = "operationName")
    val operationName: Operation = Operation.InstrumentHot,

    @Json(name = "query")
    val query: String = "query InstrumentHot(\$slug: String!, \$hasBroker: Boolean!, \$hasIIS: Boolean!) {\n  instruments {\n    metaData: bySlug(slug: \$slug) {\n      ... on AnyInstrumentItem {\n        id\n        marketData {\n          id\n          price\n          bidPrice\n          askPrice\n          timestamp\n          absoluteChange\n          percentChange\n          tradeOpen\n          sessionOpen\n          accruedInterest\n          nextSessionStateChange\n          \n        }\n        \n      }\n      \n    }\n    \n  }\n  ...PortfolioBalanceFragment\n  profile {\n    hasHistory: hasHistoryBySlug(slug: \$slug)\n    \n  }\n}\n\nfragment PortfolioBalanceFragment on Query {\n  brokerPortfolio: portfolio(agreementType: BROKER) @include(if: \$hasBroker) {\n    main {\n      currency {\n        id\n        amount\n        instrument {\n          id\n          slug\n          assetCurrencyCode\n          \n        }\n        \n      }\n      \n    }\n    position: positionBySlug(slug: \$slug) {\n      ... on AnyPortfolioPosition {\n        id\n        amount\n        \n      }\n      \n    }\n    \n  }\n  iisPortfolio: portfolio(agreementType: IIS) @include(if: \$hasIIS) {\n    main {\n      currency {\n        id\n        amount\n        instrument {\n          id\n          slug\n          assetCurrencyCode\n          \n        }\n        \n      }\n      \n    }\n    position: positionBySlug(slug: \$slug) {\n      ... on AnyPortfolioPosition {\n        id\n        amount\n        \n      }\n      \n    }\n    \n  }\n  \n}\n",

    @Json(name = "variables")
    val stockPriceVariables: StockPriceVariables
)