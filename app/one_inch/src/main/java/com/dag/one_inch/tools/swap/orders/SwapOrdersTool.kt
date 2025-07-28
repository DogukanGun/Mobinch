package com.dag.one_inch.tools.swap.orders

import com.dag.one_inch.Registery
import com.dag.one_inch.filterNotNullValues
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class SwapOrdersTool(oneinchKey: String) : BaseTool(oneinchKey) {

    companion object {
        const val BASE_URL = Registery.BASE_URL + "fusion-plus/orders/v1.0/order/"
    }

    @Tool("Get cross chain swap active orders")
    suspend fun getCrossChainActiveOrders(): ActiveOrdersResponse {
        return this.getDecodedResponse<ActiveOrdersResponse>(BASE_URL + "active")
    }

    @Tool("Get actual escrow factory contract address")
    suspend fun getEscrowFactoryContractAddress(chainId: Int = 1): EscrowFactory {
        return this.getDecodedResponse<EscrowFactory>(
            BASE_URL + "escrow",
            mapOf("chainId" to chainId)
        )
    }

    @Tool(
        """
        Get orders by maker address
        Parameters: 
        address* (path) Maker's address Example: 0x1000000000000000000000000000000000000001
        ---
        page (query) Pagination step, default: 1 (page = offset / limit) Example: 1
        limit (query) Number of active orders to receive (default: 100, max: 500)
        timestampFrom (query) timestampFrom in milliseconds for interval [timestampFrom, timestampTo) Example: 1750669979910
        timestampTo (query) timestampTo in milliseconds for interval [timestampFrom, timestampTo) Example: 1750669979910
        srcToken (query) Find history by the given source token
        dstToken (query) Find history by the given destination tokenExample: 0xc2132d05d31c914a87c6611c10748aeb04b58e8f
        withToken (query) Find history items by source or destination token Example: 0xc2132d05d31c914a87c6611c10748aeb04b58e8f
        dstChainId (query) Destination chain of cross chain Example: 137
        srcChainId (query) Source chain of cross chain Example: 1
        chainId* (query) chainId for looking by dstChainId == chainId OR srcChainId == chainId Example: 56
        """
    )
    suspend fun getOrdersByMakerAddress(
        address: String,
        page: Int?,
        limit: Int?,
        timestampFrom: Int?,
        timestampTo: Int?,
        srcToken: String?,
        dstToken: String?,
        withToken: String?,
        dstChainId: Int?,
        srcChainId: Int?,
        chainId: Int = 56
    ): GetOrderByMakerOutput {
        return this.getDecodedResponse<GetOrderByMakerOutput>(
            BASE_URL + "maker/" + address,
            mapOf(
                "page" to page,
                "limit" to limit,
                "timestampFrom" to timestampFrom,
                "timestampTo" to timestampTo,
                "srcToken" to srcToken,
                "dstToken" to dstToken,
                "withToken" to withToken,
                "dstChainId" to dstChainId,
                "srcChainId" to srcChainId,
                "chainId" to chainId
            ).filterNotNullValues()
        )
    }

    @Tool(
        """
        Get all data to perform withdrawal and cancellation
        orderHash* (path) Example: 0xa0ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e9
        """
    )
    suspend fun getAllDataAboutResolver(
        orderHash: String
    ): ResolverDataOutput {
        return this.getDecodedResponse<ResolverDataOutput>(
            BASE_URL + "secrets/" + orderHash
        )
    }

    @Tool(
        """
        Get idx of each secret that is ready for submission for specific order
        orderHash* (path) Example: 0xa0ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e9
        """
    )
    suspend fun getIdxOfEachSecretsForSpecificOrder(
        orderHash: String
    ): ReadyToAcceptSecretFills {
        return this.getDecodedResponse<ReadyToAcceptSecretFills>(
            BASE_URL + "ready-to-accept-secret-fills/" + orderHash
        )
    }

    @Tool("Get idx of each secret that is ready for submission for all orders")
    suspend fun getIdxOfEachSecretForAllOrders(): ReadyToAcceptSecretFillsForAllOrders {
        return this.getDecodedResponse<ReadyToAcceptSecretFillsForAllOrders>(
            BASE_URL + "ready-to-accept-secret-fills"
        )
    }

    @Tool("Get all data to perform a cancellation or withdrawal on public periods")
    suspend fun getAllDataToPerformOpsOnPublicPeriods(): ReadyToExecutePublicActionsOutput {
        return this.getDecodedResponse<ReadyToExecutePublicActionsOutput>(
            BASE_URL + "ready-to-execute-public-actions"
        )
    }

    @Tool(
        """
        Get order by hash
        orderHash* (path) Example: 0xa0ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e9
        """
    )
    suspend fun getOrderByHash(
        orderHash: String
    ): GetOrderFillsByHashOutput {
        return this.getDecodedResponse<GetOrderFillsByHashOutput>(
            BASE_URL + "status/$orderHash"
        )
    }

    @Tool(
        """
        Get orders by hashes
        Request Body Example:
        {
          "orderHashes": [
            "0x10ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e9",
            "0x20ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e8",
            "0x30ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e7",
            "0x40ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e6"
          ]
        }
       """
    )
    suspend fun getAllOrdersByHashes(body: OrdersByHashesInput): GetOrderFillsByHashOutput {
        return this.postDecodedResponse<GetOrderFillsByHashOutput, OrdersByHashesInput>(
            BASE_URL + "status",
            body
        )
    }
}