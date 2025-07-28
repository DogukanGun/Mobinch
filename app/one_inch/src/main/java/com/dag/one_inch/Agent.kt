package com.dag.one_inch

import com.dag.one_inch.tools.createSwapTools
import com.dag.one_inch.tools.createDomainsTools
import com.dag.one_inch.tools.createGasPriceTools
import com.dag.one_inch.tools.createTokenDetailTools
import com.dag.one_inch.tools.createTraceTools
import com.dag.one_inch.tools.swap.FusionOrdersAgent
import com.dag.one_inch.tools.swap.orders.OrdersByHashesInput
import com.dag.one_inch.tools.swap.orders.SwapOrdersTool
import kotlinx.coroutines.withTimeout

import kotlinx.serialization.json.Json
import java.util.UUID
import com.google.firebase.Firebase
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.FileDataPart
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.asTextOrNull
import com.google.firebase.ai.type.content
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.JsonObject
import com.dag.one_inch.tools.domains.DomainsTool
import com.dag.one_inch.tools.gasprice.GasPriceTool
import com.dag.one_inch.tools.token.detail.TokenDetailTools
import com.dag.one_inch.tools.traces.TraceTools

class Agent(
    var oneinchKey: String
) {
    companion object {
        const val BASE_URL = "https://api.1inch.dev/swap/v5.2/1/tokens"
        internal val json = Json { ignoreUnknownKeys = true }
        private const val REQUEST_TIMEOUT = 30_000L // 30 seconds
        private const val MAX_HISTORY = 20 // Keep last 20 messages (10 user, 10 model)
    }

    // Switched from LangChain4j memory to a simple map storing conversation history
    private val memoryMap = mutableMapOf<String, MutableList<Content>>()

    // Instantiate your tool classes directly
    private val swapOrdersTool = SwapOrdersTool(oneinchKey)
    private val domainsTool = DomainsTool(oneinchKey)
    private val gasPriceTool = GasPriceTool(oneinchKey)
    private val tokenDetailTool = TokenDetailTools(oneinchKey)
    private val traceTool = TraceTools(oneinchKey)

    @OptIn(PublicPreviewAPI::class)
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-1.5-flash",
            tools = listOf(
                createSwapTools(),
                createDomainsTools(),
                createGasPriceTools(),
                createTokenDetailTools(),
                createTraceTools()
            ).flatten()
        )

    private fun getMemory(id: String): MutableList<Content> =
        memoryMap.getOrPut(id) { mutableListOf() }

    private fun createMemory(): String {
        val newId = UUID.randomUUID().toString()
        memoryMap[newId] = mutableListOf()
        return newId
    }

    /**
     * Executes a tool call requested by the model.
     */
    private suspend fun executeToolCall(functionCall: FunctionCallPart): FunctionResponsePart {
        val functionName = functionCall.name
        val args = functionCall.args
        val result = try {
            withTimeout(REQUEST_TIMEOUT) {
                when (functionName) {
                    // Swap Orders Tools
                    "getCrossChainActiveOrders" -> swapOrdersTool.getCrossChainActiveOrders()
                    "getEscrowFactoryContractAddress" -> swapOrdersTool.getEscrowFactoryContractAddress(
                        (args["chainId"] as? Double)?.toInt() ?: 1
                    )
                    "getOrdersByMakerAddress" -> swapOrdersTool.getOrdersByMakerAddress(
                        address = args["address"] as String,
                        page = (args["page"] as? Double)?.toInt(),
                        limit = (args["limit"] as? Double)?.toInt(),
                        timestampFrom = (args["timestampFrom"] as? Double)?.toInt(),
                        timestampTo = (args["timestampTo"] as? Double)?.toInt(),
                        srcToken = args["srcToken"] as? String,
                        dstToken = args["dstToken"] as? String,
                        withToken = args["withToken"] as? String,
                        dstChainId = (args["dstChainId"] as? Double)?.toInt(),
                        srcChainId = (args["srcChainId"] as? Double)?.toInt(),
                        chainId = (args["chainId"] as Double).toInt()
                    )
                    "getAllDataAboutResolver" -> swapOrdersTool.getAllDataAboutResolver(
                        args["orderHash"] as String
                    )
                    "getIdxOfEachSecretsForSpecificOrder" -> swapOrdersTool.getIdxOfEachSecretsForSpecificOrder(
                        args["orderHash"] as String
                    )
                    "getIdxOfEachSecretForAllOrders" -> swapOrdersTool.getIdxOfEachSecretForAllOrders()
                    "getAllDataToPerformOpsOnPublicPeriods" -> swapOrdersTool.getAllDataToPerformOpsOnPublicPeriods()
                    "getOrderByHash" -> swapOrdersTool.getOrderByHash(
                        args["orderHash"] as String
                    )
                    "getAllOrdersByHashes" -> {
                        val bodyMap = args["body"] as Map<String, List<String>>
                        val hashes = bodyMap["orderHashes"]!!
                        swapOrdersTool.getAllOrdersByHashes(OrdersByHashesInput(hashes))
                    }
                    
                    // Domains Tools
                    "getAddressFromDomain" -> domainsTool.getAddressFromDomain(
                        args["name"] as String
                    )
                    "getDomainFromAddress" -> domainsTool.getDomainFromAddress(
                        args["address"] as String
                    )
                    "getProviderDataWithAvatar" -> domainsTool.getProviderDataWithAvatar(
                        args["addressOrDomain"] as String
                    )
                    "getDomainForAddresses" -> {
                        val addresses = args["addresses"] as List<String>
                        domainsTool.getDomainForAddresses(addresses)
                    }
                    
                    // Gas Price Tools
                    "getGasPrice" -> gasPriceTool.getGasPrice(
                        args["chain"] as String
                    )
                    
                    // Token Detail Tools
                    "getChainTokenInfo" -> tokenDetailTool.getChainTokenInfo(
                        args["chain"] as String
                    )
                    "getTokenInfo" -> tokenDetailTool.getTokenInfo(
                        chain = args["chain"] as String,
                        contractAddress = args["contractAddress"] as String
                    )
                    "getRangeCharts" -> tokenDetailTool.getRangeCharts(
                        args["chain"] as String
                    )
                    "getTokenRangeCharts" -> tokenDetailTool.getTokenRangeCharts(
                        chain = args["chain"] as String,
                        tokenAddress = args["tokenAddress"] as String
                    )
                    "getIntervalCharts" -> tokenDetailTool.getIntervalCharts(
                        args["chain"] as String
                    )
                    "getTokenIntervalCharts" -> tokenDetailTool.getTokenIntervalCharts(
                        chain = args["chain"] as String,
                        tokenAddress = args["tokenAddress"] as String
                    )
                    "getPriceChange" -> tokenDetailTool.getPriceChange(
                        args["chain"] as String
                    )
                    "getTokenPriceChange" -> tokenDetailTool.getTokenPriceChange(
                        chain = args["chain"] as String,
                        tokenAddress = args["tokenAddress"] as String
                    )
                    
                    // Trace Tools
                    "getSyncedInterval" -> traceTool.getSyncedInterval(
                        args["chain"] as String
                    )
                    "getBlockTrace" -> traceTool.getBlockTrace(
                        chain = args["chain"] as String,
                        blockNumber = (args["blockNumber"] as Double).toLong()
                    )
                    "getBlockTraceTx" -> traceTool.getBlockTraceTx(
                        chain = args["chain"] as String,
                        blockNumber = (args["blockNumber"] as Double).toLong(),
                        txHash = args["txHash"] as String
                    )
                    "getBlockTraceWithOffset" -> traceTool.getBlockTraceWithOffset(
                        chain = args["chain"] as String,
                        blockNumber = (args["blockNumber"] as Double).toLong(),
                        offset = (args["offset"] as Double).toLong()
                    )
                    
                    else -> error("Unknown function: $functionName")
                }
            }
        } catch (e: TimeoutCancellationException) {
            "Error: The request to the tool timed out."
        } catch (e: Exception) {
            "Error executing tool '$functionName': ${e.message}"
        }
        return FunctionResponsePart(functionName, result as JsonObject)
    }

    /**
     * Main interaction method. It's now a suspend function.
     */
    suspend fun ask(message: String, messageHistoryId: String? = null): ChatResponse {
        val historyId = messageHistoryId ?: createMemory()
        val history = getMemory(historyId)

        // Add the new user message to the history
        history.add(content(role = "user") { text(message) })

        while (true) {
            // Send history to the model
            val response = model.generateContent(*history.toTypedArray())

            val responsePart = response.candidates.first().content.parts.first()

            when (responsePart) {
                // Final answer from the model
                is TextPart -> {
                    history.add(response.candidates.first().content)
                    // Trim history if it's too long
                    if (history.size > MAX_HISTORY) {
                        memoryMap[historyId] = history.takeLast(MAX_HISTORY).toMutableList()
                    }
                    return ChatResponse(response = responsePart.text, id = historyId)
                }
                // Model wants to call a function
                is FunctionCallPart -> {
                    // Add the function call to history
                    history.add(response.candidates.first().content)
                    // Execute the function
                    val toolResponse = executeToolCall(responsePart)
                    // Add the function's response to history
                    history.add(content(role = "tool") { part(toolResponse) })
                    // Loop again to get the model's final answer based on the tool's output
                }
            }
        }
    }

    fun getMessagesByHistoryAsStrings(memoryId: String): List<String>? {
        return memoryMap[memoryId]?.mapNotNull { content ->
            val role = content.role?.replaceFirstChar { it.uppercase() } ?: "Unknown"
            val text = content.parts.joinToString(" ") { part ->
                when (part) {
                    is TextPart -> part.text
                    is FunctionCallPart -> "Function Call: ${part.name}(${part.args})"
                    is FunctionResponsePart -> "Function Result: ${part.response}"
                    else -> ""
                }
            }
            "$role: $text"
        }
    }

    fun getFirstMessageForEachMessages(): List<MessageShortcut> {
        return memoryMap.mapNotNull { (key, memory) ->
            memory.firstOrNull { it.role == "user" }?.let { firstContent ->
                MessageShortcut(
                    firstMessage = firstContent.parts.first().asTextOrNull() ?: "",
                    id = key
                )
            }
        }
    }

    fun updateApiKey(newOneinchApiKey: String) {
        this.oneinchKey = newOneinchApiKey
    }
}