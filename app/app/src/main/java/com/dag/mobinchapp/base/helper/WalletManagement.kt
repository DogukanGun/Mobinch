package com.dag.mobinchapp.base.helper

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import io.metamask.androidsdk.DappMetadata
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.EthereumState
import io.metamask.androidsdk.SDKOptions
import io.metamask.androidsdk.Result

interface WalletManagement {
    fun connect(callback: ((Result) -> Unit)?)
    fun sendRequest(request: EthereumRequest, callback: ((Result) -> Unit)?)
}

class WalletManagementImpl(
    context: Context
): WalletManagement {
    val appMetadata = DappMetadata("Mobinch", "https://www.mobinch.com")
    val infuraAPIKey = "1234567890"
    val readonlyRPCMap = mapOf("0x1" to "hptts://www.testrpc.com")
    val ethereum = Ethereum(context, appMetadata, SDKOptions(infuraAPIKey, readonlyRPCMap))

    val ethereumState = MediatorLiveData<EthereumState>().apply {
        addSource(ethereum.ethereumState) { newEthereumState ->
            value = newEthereumState
        }
    }

    override fun connect(callback: ((Result) -> Unit)?) {
        ethereum.connect(callback)
    }

    override fun sendRequest(request: EthereumRequest, callback: ((Result) -> Unit)?) {
        ethereum.sendRequest(request, callback)
    }
}