package com.dag.one_inch.tools.transaction.gateway

data class BroadcastRequest(
    val rawTransaction: String
)
data class BroadcastResponse(
    val transactionHash: String
)