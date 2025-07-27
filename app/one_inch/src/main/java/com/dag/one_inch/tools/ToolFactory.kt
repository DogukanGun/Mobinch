package com.dag.one_inch.tools

import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool

@OptIn(PublicPreviewAPI::class)
fun createSwapTools(): List<Tool> {
    val getCrossChainActiveOrdersTool = FunctionDeclaration(
        name = "getCrossChainActiveOrders",
        description = "Get cross chain swap active orders.",
        parameters = emptyMap()
    )
    val getEscrowFactoryContractAddressTool = FunctionDeclaration(
        name = "getEscrowFactoryContractAddress",
        description = "Get actual escrow factory contract address.",
        parameters = mapOf(
            "chainId" to Schema.integer(
                "Chain ID to query the factory for. Default is Ethereum Mainnet (1)."
            )
        )
    )
    val getOrdersByMakerAddressTool = FunctionDeclaration(
        name = "getOrdersByMakerAddress",
        description = "Get orders by maker address with optional filters.",
        parameters = mapOf(
            "address" to Schema.string("Maker's address (required). Example: 0x100...0001"),
            "page" to Schema.integer("Pagination page (offset / limit). Default is 1."),
            "limit" to Schema.integer("Number of orders to fetch. Default is 100, max is 500."),
            "timestampFrom" to Schema.integer("Start of the time range (milliseconds)."),
            "timestampTo" to Schema.integer("End of the time range (milliseconds)."),
            "srcToken" to Schema.string("Filter by source token."),
            "dstToken" to Schema.string("Filter by destination token."),
            "withToken" to Schema.string("Filter by either source or destination token."),
            "dstChainId" to Schema.integer("Destination chain ID for cross-chain orders."),
            "srcChainId" to Schema.integer("Source chain ID for cross-chain orders."),
            "chainId" to Schema.integer("Chain ID to filter by src or dst chain (required, default: 56).")
        )
    )
    val getAllDataAboutResolverTool = FunctionDeclaration(
        name = "getAllDataAboutResolver",
        description = "Get all data to perform withdrawal and cancellation based on order hash.",
        parameters = mapOf(
            "orderHash" to Schema.string("Hash of the order (required).")
        )
    )
    val getIdxOfEachSecretsForSpecificOrderTool = FunctionDeclaration(
        name = "getIdxOfEachSecretsForSpecificOrder",
        description = "Get indexes of each secret ready for submission for a specific order.",
        parameters = mapOf(
            "orderHash" to Schema.string("Hash of the order (required).")
        )
    )
    val getIdxOfEachSecretForAllOrdersTool = FunctionDeclaration(
        name = "getIdxOfEachSecretForAllOrders",
        description = "Get indexes of each secret that is ready for submission for all orders.",
        parameters = emptyMap()
    )
    val getAllDataToPerformOpsOnPublicPeriodsTool = FunctionDeclaration(
        name = "getAllDataToPerformOpsOnPublicPeriods",
        description = "Get all data to perform cancellation or withdrawal during public periods.",
        parameters = emptyMap()
    )
    val getOrderByHashTool = FunctionDeclaration(
        name = "getOrderByHash",
        description = "Get order status and fills using its hash.",
        parameters = mapOf(
            "orderHash" to Schema.string("Order hash (required).")
        )
    )
    val getAllOrdersByHashesTool = FunctionDeclaration(
        name = "getAllOrdersByHashes",
        description = "Get multiple orders' status and fill info by their hashes.",
        parameters = mapOf(
            "orderHashes" to Schema.array(
                items = Schema.string("Order hash as a hex string."),
                description = "A list of order hashes to query."
            )
        )
    )
    val functionDeclarationList =
        listOf(
            getCrossChainActiveOrdersTool,
            getEscrowFactoryContractAddressTool,
            getOrdersByMakerAddressTool,
            getAllDataAboutResolverTool,
            getIdxOfEachSecretsForSpecificOrderTool,
            getIdxOfEachSecretForAllOrdersTool,
            getAllDataToPerformOpsOnPublicPeriodsTool,
            getOrderByHashTool,
            getAllOrdersByHashesTool
        )
    return listOf(Tool.functionDeclarations(functionDeclarationList))
}