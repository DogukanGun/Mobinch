package com.dag.mobinchapp.features.aibot

import com.dag.mobinchapp.base.BaseVS
import java.util.UUID


sealed class AiBotVS: BaseVS {
    object Loading : AiBotVS()

    data class Success(
        val chatMessages: List<ChatMessage> = emptyList(),
        val isHeaderExpanded: Boolean = false,
        val showSwapDialog: Boolean = false,
        val suggestedActions: List<SuggestedAction> = emptyList(),
        val showStakeDialog: Boolean = false,
        val showWalletConnectionDialog: Boolean = false,
        val isWalletConnected: Boolean = false
    ) : AiBotVS()

    data class Error(val message: String) : AiBotVS()

    data class SuggestedAction(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val description: String,
        val type: ActionType,
        val params: Map<String, String> = emptyMap()
    )

    enum class ActionType {
        SWAP,
        SEND,
        STAKE,
        RESOLVER,
        VIEW_TOKEN
    }
    data class ChatMessage(
        val id: String = UUID.randomUUID().toString(),
        val content: String,
        val isFromAI: Boolean,
        val timestamp: Long = System.currentTimeMillis(),
        val messageType: MessageType = MessageType.TEXT
    )

    enum class MessageType {
        TEXT,
        ERROR,
        SUCCESS
    }

}
