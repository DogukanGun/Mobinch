package com.dag.mobinchapp.features.aibot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dag.mobinchapp.ui.theme.mainBackground


@Composable
fun AiBotScreen(
    viewModel: AiBotVM = hiltViewModel(),
) {
    val state by viewModel.viewState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackground)
    ) {
        when (state) {
            is AiBotVS.Loading -> {

            }
            is AiBotVS.Success -> {
                val successState = state as AiBotVS.Success

                MobinchAgentView(
                    state = successState,
                    onMessageSend = { viewModel.sendMessage(it) },
                    onActionExecute = { viewModel.executeAction(it) },
                    onHeaderClick = { viewModel.toggleHeader() }
                )

                // Show Wallet Connection Dialog
                if (successState.showWalletConnectionDialog) {
                    WalletConnectionDialog(
                        onDismiss = { viewModel.dismissWalletConnectionDialog() },
                        onConnect = { viewModel.connectWallet() }
                    )
                }

            }
            is AiBotVS.Error -> {

            }
            null -> TODO()
        }
    }
}