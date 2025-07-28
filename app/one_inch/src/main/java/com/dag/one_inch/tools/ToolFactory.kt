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

@OptIn(PublicPreviewAPI::class)
fun createDomainsTools(): List<Tool> {
    val getAddressFromDomainTool = FunctionDeclaration(
        name = "getAddressFromDomain",
        description = "Returns domains for address if existed",
        parameters = mapOf(
            "name" to Schema.string("Domain name to look up")
        )
    )
    val getDomainFromAddressTool = FunctionDeclaration(
        name = "getDomainFromAddress",
        description = "Returns domain for address if existed",
        parameters = mapOf(
            "address" to Schema.string("Wallet address to look up")
        )
    )
    val getProviderDataWithAvatarTool = FunctionDeclaration(
        name = "getProviderDataWithAvatar",
        description = "Get provider data with avatar for an address or domain",
        parameters = mapOf(
            "addressOrDomain" to Schema.string("Address or domain to fetch provider data")
        )
    )
    val getDomainForAddressesTool = FunctionDeclaration(
        name = "getDomainForAddresses",
        description = "Get domains for multiple addresses",
        parameters = mapOf(
            "addresses" to Schema.array(
                items = Schema.string("Wallet address"),
                description = "List of addresses to look up domains for"
            )
        )
    )
    val functionDeclarationList = listOf(
        getAddressFromDomainTool,
        getDomainFromAddressTool,
        getProviderDataWithAvatarTool,
        getDomainForAddressesTool
    )
    return listOf(Tool.functionDeclarations(functionDeclarationList))
}

@OptIn(PublicPreviewAPI::class)
fun createGasPriceTools(): List<Tool> {
    val getGasPriceTool = FunctionDeclaration(
        name = "getGasPrice",
        description = "Get gas price for a specific chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier to get gas price for")
        )
    )
    val functionDeclarationList = listOf(getGasPriceTool)
    return listOf(Tool.functionDeclarations(functionDeclarationList))
}

@OptIn(PublicPreviewAPI::class)
fun createTokenDetailTools(): List<Tool> {
    val getChainTokenInfoTool = FunctionDeclaration(
        name = "getChainTokenInfo",
        description = "Get token info for a specific chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier to get token info for")
        )
    )
    val getTokenInfoTool = FunctionDeclaration(
        name = "getTokenInfo",
        description = "Get token info for a specific contract on a chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "contractAddress" to Schema.string("Contract address of the token")
        )
    )
    val getRangeChartsTool = FunctionDeclaration(
        name = "getRangeCharts",
        description = "Get chart data (range) for all tokens on a chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier")
        )
    )
    val getTokenRangeChartsTool = FunctionDeclaration(
        name = "getTokenRangeCharts",
        description = "Get range chart data for a specific token",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "tokenAddress" to Schema.string("Token contract address")
        )
    )
    val getIntervalChartsTool = FunctionDeclaration(
        name = "getIntervalCharts",
        description = "Get chart data (interval) for all tokens on a chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier")
        )
    )
    val getTokenIntervalChartsTool = FunctionDeclaration(
        name = "getTokenIntervalCharts",
        description = "Get interval chart data for a specific token",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "tokenAddress" to Schema.string("Token contract address")
        )
    )
    val getPriceChangeTool = FunctionDeclaration(
        name = "getPriceChange",
        description = "Get price change for all tokens on a chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier")
        )
    )
    val getTokenPriceChangeTool = FunctionDeclaration(
        name = "getTokenPriceChange",
        description = "Get price change for a specific token",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "tokenAddress" to Schema.string("Token contract address")
        )
    )
    val functionDeclarationList = listOf(
        getChainTokenInfoTool,
        getTokenInfoTool,
        getRangeChartsTool,
        getTokenRangeChartsTool,
        getIntervalChartsTool,
        getTokenIntervalChartsTool,
        getPriceChangeTool,
        getTokenPriceChangeTool
    )
    return listOf(Tool.functionDeclarations(functionDeclarationList))
}

@OptIn(PublicPreviewAPI::class)
fun createTraceTools(): List<Tool> {
    val getSyncedIntervalTool = FunctionDeclaration(
        name = "getSyncedInterval",
        description = "Get synced interval for a chain",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier")
        )
    )
    val getBlockTraceTool = FunctionDeclaration(
        name = "getBlockTrace",
        description = "Get full block trace by block number",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "blockNumber" to Schema.integer("Block number to trace")
        )
    )
    val getBlockTraceTxTool = FunctionDeclaration(
        name = "getBlockTraceTx",
        description = "Get transaction trace within block by txHash",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "blockNumber" to Schema.integer("Block number"),
            "txHash" to Schema.string("Transaction hash")
        )
    )
    val getBlockTraceWithOffsetTool = FunctionDeclaration(
        name = "getBlockTraceWithOffset",
        description = "Get block trace with offset pagination",
        parameters = mapOf(
            "chain" to Schema.string("Chain identifier"),
            "blockNumber" to Schema.integer("Block number"),
            "offset" to Schema.integer("Offset for pagination")
        )
    )
    val functionDeclarationList = listOf(
        getSyncedIntervalTool,
        getBlockTraceTool,
        getBlockTraceTxTool,
        getBlockTraceWithOffsetTool
    )
    return listOf(Tool.functionDeclarations(functionDeclarationList))
}