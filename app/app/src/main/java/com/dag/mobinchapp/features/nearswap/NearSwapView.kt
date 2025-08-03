package com.dag.mobinchapp.features.nearswap

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.dag.mobinchapp.base.components.CustomButton
import com.dag.mobinchapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearSwapScreen(
    navController: NavController,
    viewModel: NearSwapVM = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsState()

    // Animation properties
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(true) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackground)
    ) {
        when (state) {
            null, NearSwapVS.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    color = gradientStart,
                    strokeWidth = 4.dp
                )
            }
            
            is NearSwapVS.Error -> {
                NearSwapErrorView(state as NearSwapVS.Error, viewModel)
            }
            
            is NearSwapVS.Success -> {
                NearSwapContent(
                    state = state as NearSwapVS.Success,
                    viewModel = viewModel,
                    animatedProgress = animatedProgress.value
                )
            }
        }
    }
}

@Composable
fun NearSwapContent(
    state: NearSwapVS.Success,
    viewModel: NearSwapVM,
    animatedProgress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .alpha(animatedProgress),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Header Section
        NearSwapHeader()
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Wallet Connection Status
        if (!state.isWalletConnected) {
            WalletConnectionCard(
                onConnectClick = { viewModel.connectWallet() }
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            ConnectedWalletCard(
                address = state.walletAddress ?: "",
                onDisconnectClick = { viewModel.disconnectWallet() }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Swap Interface
        SwapInterface(
            swapData = state.swapData,
            isSwapping = state.isSwapping,
            onFromAmountChange = { viewModel.updateFromAmount(it) },
            onToAmountChange = { viewModel.updateToAmount(it) },
            onSwapTokens = { viewModel.swapTokens() },
            onExecuteSwap = { viewModel.executeSwap() },
            isWalletConnected = state.isWalletConnected
        )
    }
}

@Composable
fun NearSwapHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo/Title
        Text(
            text = "MOBINCH",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Swap cross-chain",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = primaryText
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Perform cross-chain swaps between\ndifferent networks with ease",
            fontSize = 16.sp,
            color = secondaryText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun WalletConnectionCard(
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet",
                tint = activeAccentColor,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Connect your wallet to start swapping",
                color = secondaryText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = primaryButtonColor,
                text = "Connect Wallet",
                onClick = onConnectClick
            )
        }
    }
}

@Composable
fun ConnectedWalletCard(
    address: String,
    onDisconnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Wallet",
                tint = successColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wallet Connected",
                    color = primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${address.take(6)}...${address.takeLast(4)}",
                    color = secondaryText,
                    fontSize = 12.sp
                )
            }
            
            TextButton(
                onClick = onDisconnectClick
            ) {
                Text(
                    text = "Disconnect",
                    color = errorColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SwapInterface(
    swapData: SwapData,
    isSwapping: Boolean,
    onFromAmountChange: (String) -> Unit,
    onToAmountChange: (String) -> Unit,
    onSwapTokens: () -> Unit,
    onExecuteSwap: () -> Unit,
    isWalletConnected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // From Section
            Text(
                text = "From",
                color = secondaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TokenInputCard(
                amount = swapData.fromAmount,
                token = swapData.fromToken,
                onAmountChange = onFromAmountChange,
                isReadOnly = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Swap Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onSwapTokens() },
                    shape = CircleShape,
                    color = primaryButtonColor
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // To Section
            Text(
                text = "To",
                color = secondaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TokenInputCard(
                amount = swapData.toAmount,
                token = swapData.toToken,
                onAmountChange = onToAmountChange,
                isReadOnly = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Exchange Rate
            if (swapData.rate.isNotEmpty()) {
                Text(
                    text = swapData.rate,
                    color = secondaryText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Swap Button
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isWalletConnected) primaryButtonColor else inactiveAccentColor,
                text = when {
                    !isWalletConnected -> "Connect Wallet First"
                    isSwapping -> "Swapping..."
                    else -> "Swap"
                },
                enabled = isWalletConnected && !isSwapping,
                onClick = onExecuteSwap
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenInputCard(
    amount: String,
    token: TokenInfo,
    onAmountChange: (String) -> Unit,
    isReadOnly: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("0.0", color = disabledText) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryText,
                    unfocusedTextColor = primaryText,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                singleLine = true,
                readOnly = isReadOnly
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Token Selector
            Surface(
                modifier = Modifier.clickable { /* TODO: Open token selector */ },
                shape = RoundedCornerShape(12.dp),
                color = activeAccentColor
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = token.symbol,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Token",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NearSwapErrorView(
    state: NearSwapVS.Error,
    viewModel: NearSwapVM
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = state.message,
            fontSize = 16.sp,
            color = secondaryText,
            textAlign = TextAlign.Center
        )
        
        if (state.canRetry) {
            Spacer(modifier = Modifier.height(24.dp))
            
            CustomButton(
                backgroundColor = primaryButtonColor,
                text = "Try Again",
                onClick = { viewModel.retry() }
            )
        }
    }
}

@Preview
@Composable
fun NearSwapScreenPreview() {
    NearSwapScreen(
        navController = rememberNavController()
    )
}
