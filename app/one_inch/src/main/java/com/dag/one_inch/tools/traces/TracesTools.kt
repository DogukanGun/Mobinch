package com.dag.one_inch.tools.traces

import com.dag.one_inch.Registery
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class TraceTools(private val oneInchKey: String) : BaseTool(oneInchKey) {

    companion object {
        private const val BASE = "${Registery.BASE_URL}traces/v1.0"
    }

    @Tool("Get synced interval for chain")
    suspend fun getSyncedInterval(chain: String): ReadSyncedIntervalResponseDto {
        return getDecodedResponse<ReadSyncedIntervalResponseDto>(
            "$BASE/chain/$chain/synced-interval"
        )
    }

    @Tool("Get full block trace by block number")
    suspend fun getBlockTrace(chain: String, blockNumber: Long): Any {
        return getDecodedResponse<Any>(
            "$BASE/chain/$chain/block-trace/$blockNumber"
        )
    }

    @Tool("Get transaction trace within block by txHash")
    suspend fun getBlockTraceTx(
        chain: String,
        blockNumber: Long,
        txHash: String
    ): PlainTransactionTraceWithTypeDto {
        return getDecodedResponse<PlainTransactionTraceWithTypeDto>(
            "$BASE/chain/$chain/block-trace/$blockNumber/tx-hash/$txHash"
        )
    }

    @Tool("Get block trace with offset pagination")
    suspend fun getBlockTraceWithOffset(
        chain: String,
        blockNumber: Long,
        offset: Long
    ): PlainTransactionTraceWithTypeDto {
        return getDecodedResponse<PlainTransactionTraceWithTypeDto>(
            "$BASE/chain/$chain/block-trace/$blockNumber/offset/$offset"
        )
    }
}