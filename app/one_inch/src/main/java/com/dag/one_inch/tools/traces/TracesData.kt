package com.dag.one_inch.tools.traces

data class ReadSyncedIntervalResponseDto(
    val from: Long,
    val to: Long
)

data class CoreBuiltinBlockTracesDto(
    val type: String,
    val version: String,
    val number: Long,
    val blockHash: String,
    val blockTimestamp: String,
    val traces: List<CoreBuiltinTransactionRootSuccessTraceDto>
)

data class CoreBuiltinTransactionRootSuccessTraceDto(
    val chainId: Long,
    val type: String,
    val from: String,
    val to: String,
    val value: String,
    val gas: String,
    val gasUsed: String,
    val input: String,
    val output: String,
    val calls: List<CoreBuiltinCallTraceDto>,
    val txHash: String,
    val nonce: String,
    val error: String,
    val revertReason: String,
    val gasPrice: String,
    val maxFeePerGas: String,
    val maxPriorityFeePerGas: String,
    val gasHex: String,
    val events: List<CoreBuiltinTraceLogDto>
)

data class CoreBuiltinCallTraceDto(
    val isParentHasError: Boolean,
    val type: String,
    val from: String,
    val to: String,
    val value: String,
    val gas: String,
    val gasUsed: String,
    val input: String,
    val output: String,
    val error: String,
    val calls: List<CoreBuiltinCallTraceDto>
)

data class CoreBuiltinTraceLogDto(
    val data: String,
    val topics: List<List<String>>,
    val contract: String
)

data class CoreCustomBlockTraceDto(
    val type: String,
    val version: String,
    val number: Long,
    val blockHash: String,
    val blockTimestamp: String,
    val traces: List<Any> // Use sealed classes for typed structure
)

data class CoreCustomErrorTransactionTraceDto(
    val error: String,
    val txHash: String,
    val nonce: String,
    val gasPrice: String,
    val maxFeePerGas: String,
    val maxPriorityFeePerGas: String,
    val gasHex: String
)

data class CoreCustomRootTxEventCallstackTraceFullDto(
    val type: String,
    val from: String,
    val to: String,
    val value: String,
    val gasLimit: Long,
    val gasUsed: Long,
    val input: String,
    val output: String,
    val time: String,
    val calls: List<CoreCustomTxEventCallstackTraceDto>,
    val logs: List<CoreCustomTraceLogDto>,
    val status: String,
    val success: Int,
    val res: String,
    val depth: Int,
    val destructAddress: String,
    val errorDetails: String,
    val storage: List<CustomStorageDto>,
    val prevGasLimit: Long,
    val gas: String,
    val gasCost: Long,
    val address: String
)

data class CoreCustomTxEventCallstackTraceDto(
    val type: String,
    val from: String,
    val to: String,
    val value: String,
    val gasLimit: Long,
    val gasUsed: Long,
    val input: String,
    val output: String,
    val time: String,
    val calls: List<CoreCustomTxEventCallstackTraceDto>
)

data class CoreCustomTraceLogDto(
    val data: String,
    val topics: List<List<String>>,
    val contract: String
)

data class CustomStorageDto(
    val type: String,
    val key: String,
    val value: String
)

data class PlainTransactionTraceWithTypeDto(
    val transactionTrace: List<Any>,
    val type: String
)