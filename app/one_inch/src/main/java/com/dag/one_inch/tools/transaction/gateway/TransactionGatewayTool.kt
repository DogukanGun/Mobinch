package com.dag.one_inch.tools.transaction.gateway

import com.dag.one_inch.Registery
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class TransactionGatewayTool(oneinchKey: String) : BaseTool(oneinchKey) {

    companion object {
        const val BASE_URL = Registery.BASE_URL + "tx-gateway/v1.1/"
    }

    @Tool("Broadcast public transaction")
    suspend fun broadcastPublicTransaction(
        chainId: String,
        body: BroadcastRequest
    ): BroadcastResponse{
        return this.postDecodedResponse<BroadcastResponse, BroadcastRequest>(
            "$BASE_URL$chainId/broadcast",
            body
        )
    }

    @Tool("Broadcast public transaction")
    suspend fun broadcastPrivateTransaction(
        chainId: String,
        body: BroadcastRequest
    ): BroadcastResponse{
        return this.postDecodedResponse<BroadcastResponse, BroadcastRequest>(
            "$BASE_URL$chainId/flashbots",
            body
        )
    }
}