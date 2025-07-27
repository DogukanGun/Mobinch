package com.dag.mobinchapp.features.aibot

import androidx.lifecycle.viewModelScope
import com.dag.mobinchapp.BuildConfig
import com.dag.one_inch.Agent
import com.dag.mobinchapp.base.BaseVM
import com.dag.mobinchapp.base.components.bottomnav.BottomNavMessageManager
import com.dag.mobinchapp.base.helper.WalletManagement
import com.dag.mobinchapp.base.scroll.ScrollStateManager
import com.dag.mobinchapp.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.metamask.androidsdk.Result

@HiltViewModel
class AiBotVM @Inject constructor(
    private val scrollManager: ScrollStateManager,
    private val bottomNavManager: BottomNavMessageManager,
    private val walletManagementImpl: WalletManagement
): BaseVM<AiBotVS>(AiBotVS.Loading) {

    init {
        scrollManager.updateScrolling(true)
        initializeAgent()
    }

    private val agent = Agent(BuildConfig.open_ai_key, BuildConfig.oneinch_key)

    private fun initializeAgent() {
        viewModelScope.launch {
            try {
                bottomNavManager.showMessage("1inch AI Agent")
                _viewState.value = AiBotVS.Success(
                    chatMessages = listOf(
                        AiBotVS.ChatMessage(
                            content = "Hi! I'm your 1inch AI Agent. What would you like to do?",
                            isFromAI = true
                        )
                    ),
                    suggestedActions = emptyList()
                )
            } catch (e: Exception) {
                _viewState.value = AiBotVS.Error(e.message ?: "Failed to initialize 1inch Agent")
            }
        }
    }

    fun connectWallet() {
        val currentState = _viewState.value
        walletManagementImpl.connect {
            when(it) {
                is Result.Success -> {
                    _viewState.value = (currentState as AiBotVS.Success).copy(
                        isWalletConnected = true
                    )
                }
                is Result.Error -> {
                    viewModelScope.launch {
                        bottomNavManager.showMessage("Wallet Connection Error")
                    }
                }
            }
        }
    }

    fun chooseMemory(memoryId: String) {
        if (_viewState.value is AiBotVS.Success) {
            val currentState = _viewState.value as AiBotVS.Success
//            val messageHistory = agent.getMessagesByHistory(memoryId)
//            val messages: List<AiBotVS.ChatMessage> = messageHistory?.map { chatMessage ->
//                val isFromAi = when(chatMessage){
//                    is UserMessage -> false
//                    is AiMessage -> true
//                    is SystemMessage -> true
//                    else -> false
//                }
//                val text = when (chatMessage) {
//                    is UserMessage -> chatMessage.singleText()
//                    is AiMessage -> chatMessage.text()
//                    else -> "----"
//                }
//                AiBotVS.ChatMessage(
//                    content = text,
//                    isFromAI = isFromAi,
//                )
//            } ?: emptyList()
//            _viewState.value = currentState.copy(
//                chatMessages = messages,
//                selectedMessageId = memoryId
//            )
        } else {
            //TODO show error message
        }
    }

    fun sendMessage(content: String) {
        if (_viewState.value is AiBotVS.Success) {
            val currentState = _viewState.value as AiBotVS.Success
            viewModelScope.launch {
                val response = agent.ask(content, currentState.selectedMessageId)
                if (currentState.selectedMessageId == null) {
                    currentState.selectedMessageId = response.id
                }
                val newMessageList = currentState.chatMessages.toMutableList().apply {
                    this.add(AiBotVS.ChatMessage(
                        content = response.response,
                        isFromAI = true
                    ))
                }
                _viewState.value = currentState.copy(
                    selectedMessageId = response.id,
                    chatMessages = newMessageList
                )
            }

        } else {
           _viewState.value = AiBotVS.Error("Unknown error, please restart the app")
        }
    }

    fun dismissWalletConnectionDialog() {
        val currentState = _viewState.value
        if (currentState is AiBotVS.Success) {
            _viewState.value = currentState.copy(showWalletConnectionDialog = false)
        }
    }

    fun executeAction(action: AiBotVS.SuggestedAction) {
        throw NotImplementedError()
    }

    fun toggleHeader() {
        val currentState = _viewState.value
        if (currentState is AiBotVS.Success) {
            _viewState.value = currentState.copy(
                isHeaderExpanded = !currentState.isHeaderExpanded
            )
        }
    }
}