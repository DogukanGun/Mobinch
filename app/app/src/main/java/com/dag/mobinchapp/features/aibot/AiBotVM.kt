package com.dag.mobinchapp.features.aibot

import androidx.lifecycle.viewModelScope
import com.dag.mobinchapp.base.BaseVM
import com.dag.mobinchapp.base.components.bottomnav.BottomNavMessageManager
import com.dag.mobinchapp.base.scroll.ScrollStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiBotVM @Inject constructor(
    private val scrollManager: ScrollStateManager,
    private val bottomNavManager: BottomNavMessageManager
): BaseVM<AiBotVS>(AiBotVS.Loading) {

    init {
        scrollManager.updateScrolling(true)
        initializeAgent()
    }

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
        throw NotImplementedError()
    }

    fun sendMessage(content: String) {
        throw NotImplementedError()
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