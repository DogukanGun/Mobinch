package com.dag.mobinchapp.features.nearswap

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dag.mobinchapp.base.BaseVM
import com.dag.mobinchapp.base.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import kotlinx.coroutines.delay

@Serializable
data class ResolverStatusResponse(
    val status: String,
    val network: String? = null,
    val minProfitBps: Int? = null
)

@Serializable
data class StartResolverRequest(
    val network: String,
    val minProfitBps: Int
)

@Serializable
data class CreateOrderRequest(
    val params: OrderParams
)

@Serializable
data class OrderParams(
    val fromTokenAddress: String,
    val toTokenAddress: String,
    val amount: String,
    val walletAddress: String,
    val source: String
)

@Serializable
data class CreateOrderResponse(
    val success: Boolean,
    val orderId: String? = null,
    val message: String? = null
)

@HiltViewModel
class NearSwapVM @Inject constructor(
    private val httpClient: HttpClient,
    private val logger: Logger
) : BaseVM<NearSwapVS>(initialValue = NearSwapVS.initial()) {

    companion object {
        const val NEARSWAP_RESOLVER_STATUS = "NEARSWAP_RESOLVER_STATUS"
        const val NEARSWAP_CREATE_ORDER = "NEARSWAP_CREATE_ORDER"
        const val NEARSWAP_START_RESOLVER = "NEARSWAP_START_RESOLVER"
        
        private const val BASE_URL = "https://c3a0385c6e74519c47613a009577382d3e5b77f2-3000.dstack-prod7.phala.network"
    }

    init {
        checkResolverStatus()
    }

    fun updateFromAmount(amount: String) {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            // Simulate rate calculation (in real app, this would call an API)
            val toAmount = try {
                val fromAmountDouble = amount.toDoubleOrNull() ?: 0.0
                val rate = 28.46 // Mock rate
                (fromAmountDouble * rate).toString()
            } catch (e: Exception) {
                "0.0"
            }
            
            _viewState.value = currentState.copy(
                swapData = currentState.swapData.copy(
                    fromAmount = amount,
                    toAmount = toAmount
                )
            )
        }
    }

    fun updateToAmount(amount: String) {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            // Simulate reverse rate calculation
            val fromAmount = try {
                val toAmountDouble = amount.toDoubleOrNull() ?: 0.0
                val rate = 28.46 // Mock rate
                (toAmountDouble / rate).toString()
            } catch (e: Exception) {
                "0.0"
            }
            
            _viewState.value = currentState.copy(
                swapData = currentState.swapData.copy(
                    fromAmount = fromAmount,
                    toAmount = amount
                )
            )
        }
    }

    fun swapTokens() {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            _viewState.value = currentState.copy(
                swapData = currentState.swapData.copy(
                    fromToken = currentState.swapData.toToken,
                    toToken = currentState.swapData.fromToken,
                    fromAmount = currentState.swapData.toAmount,
                    toAmount = currentState.swapData.fromAmount
                )
            )
        }
    }

    fun executeSwap() {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            viewModelScope.launch {
                try {
                    _viewState.value = currentState.copy(isSwapping = true)
                    
                    // First, ensure resolver is running
                    startResolver()
                    
                    // Wait a bit for resolver to start
                    delay(2000)
                    
                    // Create the swap order
                    val orderRequest = CreateOrderRequest(
                        params = OrderParams(
                            fromTokenAddress = currentState.swapData.fromToken.address,
                            toTokenAddress = currentState.swapData.toToken.address,
                            amount = currentState.swapData.fromAmount,
                            walletAddress = currentState.walletAddress ?: "0x7D4CD93532c0469AE55Ad7138Df6f20D13F33E9f",
                            source = "mobinch-app"
                        )
                    )
                    
                    val response = httpClient.post("$BASE_URL/api/fusion/resolver/create-order") {
                        setBody(orderRequest)
                    }
                    
                    val result = response.body<CreateOrderResponse>()
                    
                    if (result.success) {
                        logger.logSuccess(NEARSWAP_CREATE_ORDER, "Swap order created successfully: ${result.orderId}")
                        _viewState.value = currentState.copy(
                            isSwapping = false
                        )
                    } else {
                        throw Exception(result.message ?: "Failed to create swap order")
                    }
                    
                } catch (e: Exception) {
                    logger.logError(NEARSWAP_CREATE_ORDER, "Error creating swap order: ${e.message}")
                    _viewState.value = NearSwapVS.Error(
                        message = e.message ?: "Failed to execute swap"
                    )
                }
            }
        }
    }

    private fun checkResolverStatus() {
        viewModelScope.launch {
            try {
                val response = httpClient.get("$BASE_URL/api/fusion/resolver/status")
                val status = response.body<ResolverStatusResponse>()
                
                logger.logSuccess(NEARSWAP_RESOLVER_STATUS, "Resolver status: ${status.status}")
                
                if (status.status != "running") {
                    startResolver()
                }
                
            } catch (e: Exception) {
                logger.logError(NEARSWAP_RESOLVER_STATUS, "Error checking resolver status: ${e.message}")
                // Try to start resolver anyway
                startResolver()
            }
        }
    }

    private suspend fun startResolver() {
        try {
            val startRequest = StartResolverRequest(
                network = "binance",
                minProfitBps = 100
            )
            
            val response = httpClient.post("$BASE_URL/api/fusion/resolver/start") {
                setBody(startRequest)
            }
            
            logger.logSuccess(NEARSWAP_START_RESOLVER, "Resolver start request sent")
            
        } catch (e: Exception) {
            logger.logError(NEARSWAP_START_RESOLVER, "Error starting resolver: ${e.message}")
        }
    }

    fun connectWallet() {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            // Mock wallet connection
            _viewState.value = currentState.copy(
                isWalletConnected = true,
                walletAddress = "0x7D4CD93532c0469AE55Ad7138Df6f20D13F33E9f"
            )
        }
    }

    fun disconnectWallet() {
        val currentState = _viewState.value
        if (currentState is NearSwapVS.Success) {
            _viewState.value = currentState.copy(
                isWalletConnected = false,
                walletAddress = null
            )
        }
    }

    fun retry() {
        _viewState.value = NearSwapVS.initial()
        checkResolverStatus()
    }
}
