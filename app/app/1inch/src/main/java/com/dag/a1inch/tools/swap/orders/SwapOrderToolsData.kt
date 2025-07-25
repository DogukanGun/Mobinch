package com.dag.a1inch.tools.swap.orders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//getCrossChainActiveOrders
@Serializable
data class ActiveOrdersResponse(
    @SerialName("orders") val orders: List<OrderRecord>,
    @SerialName("total") val total: Int
)

@Serializable
data class OrderRecord(
    @SerialName("order_id") val orderId: String,
    @SerialName("maker") val maker: String,
    @SerialName("taker") val taker: String? = null,
    @SerialName("src_chain") val srcChain: String,
    @SerialName("dst_chain") val dstChain: String,
    @SerialName("src_token") val srcToken: String,
    @SerialName("dst_token") val dstToken: String,
    @SerialName("src_amount") val srcAmount: String,
    @SerialName("dst_amount") val dstAmount: String,
    @SerialName("filled_src_amount") val filledSrcAmount: String? = null,
    @SerialName("filled_dst_amount") val filledDstAmount: String? = null,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("price_curve") val priceCurve: PriceCurve? = null,
    @SerialName("auction_params") val auctionParams: AuctionParams? = null,
    @SerialName("safety_deposit") val safetyDeposit: String? = null,
    @SerialName("timelocks") val timelocks: Timelocks? = null,
)

@Serializable
data class PriceCurve(
    @SerialName("start_rate") val startRate: String,
    @SerialName("min_rate") val minRate: String,
    @SerialName("decrease_rate") val decreaseRate: String,
    @SerialName("segments") val segments: List<CurveSegment>? = null
)

@Serializable
data class CurveSegment(
    @SerialName("duration") val duration: Long,
    @SerialName("rate") val rate: String
)

@Serializable
data class AuctionParams(
    @SerialName("start_timestamp") val startTimestamp: Long,
    @SerialName("waiting_period") val waitingPeriod: Long
)

@Serializable
data class Timelocks(
    @SerialName("src_timelock") val srcTimelock: Long,
    @SerialName("dst_timelock") val dstTimelock: Long
)

@Serializable
data class EscrowFactory(
    @SerialName("address") val address: String
)

@Serializable
data class GetOrderByMakerOutput(
    @SerialName("meta") val meta: Meta,
    @SerialName("items") val items: List<ActiveOrdersOutput>
)

@Serializable
data class Meta(
    @SerialName("totalItems") val totalItems: Int,
    @SerialName("itemsPerPage") val itemsPerPage: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("currentPage") val currentPage: Int
)

@Serializable
data class ActiveOrdersOutput(
    @SerialName("orderHash") val orderHash: String,
    @SerialName("signature") val signature: String,
    @SerialName("deadline") val deadline: Long,
    @SerialName("auctionStartDate") val auctionStartDate: Long,
    @SerialName("auctionEndDate") val auctionEndDate: Long,
    @SerialName("quoteId") val quoteId: String,
    @SerialName("remainingMakerAmount") val remainingMakerAmount: String,
    @SerialName("makerBalance") val makerBalance: String,
    @SerialName("makerAllowance") val makerAllowance: String,
    @SerialName("isMakerContract") val isMakerContract: Boolean,
    @SerialName("extension") val extension: String,
    @SerialName("srcChainId") val srcChainId: Long,
    @SerialName("dstChainId") val dstChainId: Long,
    @SerialName("order") val order: CrossChainOrderDto
)

@Serializable
data class CrossChainOrderDto(
    @SerialName("salt") val salt: String,
    @SerialName("maker") val maker: String,
    @SerialName("receiver") val receiver: String,
    @SerialName("makerAsset") val makerAsset: String,
    @SerialName("takerAsset") val takerAsset: String,
    @SerialName("makingAmount") val makingAmount: String,
    @SerialName("takingAmount") val takingAmount: String,
    @SerialName("makerTraits") val makerTraits: String,
    @SerialName("secretHashes") val secretHashes: List<List<String>>,
    @SerialName("fills") val fills: List<String>
)

@Serializable
data class ResolverDataOutput(
    @SerialName("orderType") val orderType: String, // Enum: SingleFill, MultipleFills
    @SerialName("secrets") val secrets: List<PublicSecret>,
    @SerialName("srcImmutables") val srcImmutables: Immutables,
    @SerialName("dstImmutables") val dstImmutables: Immutables
)

@Serializable
data class PublicSecret(
    @SerialName("idx") val idx: Int,
    @SerialName("secret") val secret: String
)

@Serializable
data class Immutables(
    @SerialName("orderHash") val orderHash: String,
    @SerialName("hashlock") val hashlock: String,
    @SerialName("maker") val maker: String,
    @SerialName("taker") val taker: String,
    @SerialName("token") val token: String,
    @SerialName("amount") val amount: String,
    @SerialName("safetyDeposit") val safetyDeposit: String,
    @SerialName("timelocks") val timelocks: String,
    @SerialName("secretHashes") val secretHashes: List<List<String>>? = null // Only present in some cases
)

@Serializable
data class ReadyToAcceptSecretFills(
    @SerialName("fills") val fills: List<ReadyToAcceptSecretFill>
)

@Serializable
data class ReadyToAcceptSecretFill(
    @SerialName("idx") val idx: Int,
    @SerialName("srcEscrowDeployTxHash") val srcEscrowDeployTxHash: String,
    @SerialName("dstEscrowDeployTxHash") val dstEscrowDeployTxHash: String
)

@Serializable
data class ReadyToAcceptSecretFillsForAllOrders(
    @SerialName("orders") val orders: List<ReadyToAcceptSecretFillsForOrder>
)

@Serializable
data class ReadyToAcceptSecretFillsForOrder(
    @SerialName("orderHash") val orderHash: String,
    @SerialName("makerAddress") val makerAddress: String,
    @SerialName("fills") val fills: List<ReadyToAcceptSecretFill>
)

@Serializable
data class ReadyToExecutePublicActionsOutput(
    @SerialName("actions") val actions: List<ReadyToExecutePublicAction>
)

@Serializable
data class ReadyToExecutePublicAction(
    @SerialName("action") val action: String, // Enum: withdraw, cancel
    @SerialName("immutables") val immutables: ImmutablesWithChain,
    @SerialName("escrow") val escrow: String,
    @SerialName("secret") val secret: String? = null
)

@Serializable
data class ImmutablesWithChain(
    @SerialName("orderHash") val orderHash: String,
    @SerialName("hashlock") val hashlock: String,
    @SerialName("maker") val maker: String,
    @SerialName("taker") val taker: String,
    @SerialName("token") val token: String,
    @SerialName("amount") val amount: String,
    @SerialName("safetyDeposit") val safetyDeposit: String,
    @SerialName("timelocks") val timelocks: String,
    @SerialName("chainId") val chainId: Long
)

@Serializable
data class GetOrderFillsByHashOutput(
    @SerialName("orderHash") val orderHash: String,
    @SerialName("status") val status: String, // Enum
    @SerialName("validation") val validation: String, // Enum
    @SerialName("order") val order: LimitOrderV4StructOutput
)

@Serializable
data class LimitOrderV4StructOutput(
    @SerialName("salt") val salt: String,
    @SerialName("maker") val maker: String,
    @SerialName("receiver") val receiver: String,
    @SerialName("makerAsset") val makerAsset: String,
    @SerialName("takerAsset") val takerAsset: String,
    @SerialName("makingAmount") val makingAmount: String,
    @SerialName("takingAmount") val takingAmount: String,
    @SerialName("makerTraits") val makerTraits: String,
    @SerialName("extension") val extension: String,
    @SerialName("points") val points: AuctionPointOutput,
    @SerialName("fills") val fills: List<FillOutputDto>
)

@Serializable
data class AuctionPointOutput(
    @SerialName("delay") val delay: Long,
    @SerialName("coefficient") val coefficient: Long,
    @SerialName("approximateTakingAmount") val approximateTakingAmount: String,
    @SerialName("positiveSurplus") val positiveSurplus: String
)

@Serializable
data class FillOutputDto(
    @SerialName("status") val status: String, // Enum
    @SerialName("txHash") val txHash: String,
    @SerialName("filledMakerAmount") val filledMakerAmount: String,
    @SerialName("filledAuctionTakerAmount") val filledAuctionTakerAmount: String,
    @SerialName("escrowEvents") val escrowEvents: List<EscrowEventDataOutput>
)

@Serializable
data class EscrowEventDataOutput(
    @SerialName("transactionHash") val transactionHash: String,
    @SerialName("escrow") val escrow: String,
    @SerialName("side") val side: String, // Enum: src, dst
    @SerialName("action") val action: String, // Enum
    @SerialName("blockTimestamp") val blockTimestamp: Long,
    @SerialName("auctionStartDate") val auctionStartDate: Long,
    @SerialName("auctionDuration") val auctionDuration: Long,
    @SerialName("initialRateBump") val initialRateBump: Long,
    @SerialName("createdAt") val createdAt: Long,
    @SerialName("srcTokenPriceUsd") val srcTokenPriceUsd: Map<String, Any>,
    @SerialName("dstTokenPriceUsd") val dstTokenPriceUsd: Map<String, Any>,
    @SerialName("cancelTx") val cancelTx: Map<String, Any>,
    @SerialName("srcChainId") val srcChainId: Long,
    @SerialName("dstChainId") val dstChainId: Long,
    @SerialName("cancelable") val cancelable: Boolean,
    @SerialName("takerAsset") val takerAsset: String,
    @SerialName("timeLocks") val timeLocks: String
)

@Serializable
data class OrdersByHashesInput(
    val orderHashes: List<String>
)