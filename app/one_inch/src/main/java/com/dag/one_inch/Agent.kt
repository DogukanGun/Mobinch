package com.dag.one_inch

import com.dag.one_inch.tools.swap.FusionOrdersAgent
import com.dag.one_inch.tools.swap.orders.SwapOrdersTool
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O
import dev.langchain4j.service.AiServices
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.header
import kotlinx.serialization.json.Json
import java.util.UUID
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.message.AiMessage


class Agent(
    openApiKey: String,
    oneinchKey: String
) {
    companion object {
        const val BASE_URL = "https://api.1inch.dev/swap/v5.2/1/tokens"
        internal val json = Json { ignoreUnknownKeys = true }
    }

    val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
        defaultRequest {
            header("Authorization", "Bearer $oneinchKey")
            accept(io.ktor.http.ContentType.Application.Json)
        }
    }

    private val memoryMap = mutableMapOf<String, MessageWindowChatMemory>()

    private var openAiModel: ChatModel? = OpenAiChatModel.builder()
        .apiKey(openApiKey)
        .modelName(GPT_4_O)
        .strictTools(true)
        .logRequests(true)
        .logResponses(true)
        .build()

    private fun getMemory(id: String): MessageWindowChatMemory {
        return memoryMap.getOrPut(id) {
            MessageWindowChatMemory.withMaxMessages(10)
        }
    }

    private fun createMemory(): String {
        val newId = UUID.randomUUID().toString()
        memoryMap[newId] = MessageWindowChatMemory.withMaxMessages(10)
        return newId
    }

    val swapOrdersTool = SwapOrdersTool(this)

    fun ask(message: String, messageHistoryId: String? = null): ChatResponse {
        val historyId = messageHistoryId ?: createMemory()
        val memory = getMemory(historyId)
        memory.add(UserMessage.from(message))
        val fusionAgent = AiServices.builder(FusionOrdersAgent::class.java)
            .chatModel(openAiModel)
            .tools(listOf(swapOrdersTool))
            .chatMemory(memory)
            .build()
        val response = fusionAgent.chat(message)
        memory.add(AiMessage.from(response))
        return ChatResponse(response = response, id = historyId)
    }

    fun getMessagesByHistory(memoryId: String): List<ChatMessage>? {
        return memoryMap[memoryId]?.messages()
    }

    fun getMessagesByHistoryAsStrings(memoryId: String): List<String>? {
        return memoryMap[memoryId]?.messages()?.mapNotNull { msg ->
            when (msg) {
                is UserMessage -> msg.singleText()
                is AiMessage -> msg.text()
                else -> null
            }
        }
    }

    fun getFirstMessageForEachMessages(): List<MessageShortcut> {
        return memoryMap.mapNotNull { (key, memory) ->
            val messages = memory.messages()
            val firstMessage = messages.firstOrNull()
            if (firstMessage is UserMessage) {
                MessageShortcut(
                    firstMessage = firstMessage.singleText(),
                    id = key
                )
            } else {
                null
            }
        }
    }

    fun getAgentClient(): HttpClient = client

}