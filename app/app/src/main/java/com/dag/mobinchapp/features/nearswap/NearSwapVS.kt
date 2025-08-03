package com.dag.mobinchapp.features.nearswap

import com.dag.mobinchapp.base.BaseVS

data class TokenInfo(
    val symbol: String,
    val name: String,
    val address: String,
    val decimals: Int,
    val logoUrl: String? = null
)

data class SwapData(
    val fromToken: TokenInfo,
    val toToken: TokenInfo,
    val fromAmount: String,
    val toAmount: String,
    val rate: String,
    val gasPrice: String? = null,
    val isLoading: Boolean = false
)

sealed class NearSwapVS : BaseVS {
    data object Loading : NearSwapVS()
    
    data class Success(
        val swapData: SwapData,
        val isWalletConnected: Boolean = false,
        val walletAddress: String? = null,
        val availableFromTokens: List<TokenInfo> = emptyList(),
        val availableToTokens: List<TokenInfo> = emptyList(),
        val isSwapping: Boolean = false,
        val showTokenSelector: Boolean = false,
        val selectingTokenType: String? = null // "from" or "to"
    ) : NearSwapVS()
    
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : NearSwapVS()

    companion object {
        fun initial() = Success(
            swapData = SwapData(
                fromToken = TokenInfo(
                    symbol = "ETH",
                    name = "Ethereum",
                    address = "0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c",
                    decimals = 18
                ),
                toToken = TokenInfo(
                    symbol = "NEAR",
                    name = "NEAR Protocol",
                    address = "0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d",
                    decimals = 24
                ),
                fromAmount = "0.5",
                toAmount = "14.23",
                rate = "1 ETH = 28.46 NEAR"
            )
        )
    }
}
