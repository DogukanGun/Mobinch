package com.dag.mobinchapp.features.aibot

import androidx.lifecycle.viewModelScope
import com.dag.mobinchapp.BuildConfig
import com.dag.one_inch.Agent
import com.dag.mobinchapp.base.BaseVM
import com.dag.mobinchapp.base.components.bottomnav.BottomNavMessageManager
import com.dag.mobinchapp.base.helper.WalletManagement
import com.dag.mobinchapp.base.scroll.ScrollStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun sendMessage(content: String) {
        val agent = Agent(BuildConfig.open_ai_key, BuildConfig.oneinch_key)
        agent.ask(content,"")
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