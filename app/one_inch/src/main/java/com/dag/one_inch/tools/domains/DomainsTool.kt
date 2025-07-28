package com.dag.one_inch.tools.domains

import com.dag.one_inch.Registery
import com.dag.one_inch.tools.BaseTool
import dev.langchain4j.agent.tool.Tool

class DomainsTool(oneinchKey: String) : BaseTool(oneinchKey)  {

    companion object {
        const val BASE_URL = Registery.BASE_URL + "domains/v2.0/"
    }

    @Tool("Returns domains for address if existed")
    suspend fun getAddressFromDomain(
        name: String
    ): ResponseV2Dto{
        return this.getDecodedResponse<ResponseV2Dto>(
            BASE_URL + "lookup",
            mapOf("name" to name)
        )
    }

    @Tool("Returns domain for address if existed\n")
    suspend fun getDomainFromAddress(
        address: String
    ): ResponseReverseV2Dto{
        return this.getDecodedResponse<ResponseReverseV2Dto>(
            BASE_URL + "reverse-lookup",
            mapOf("address" to address)
        )
    }

    suspend fun getProviderDataWithAvatar(
        addressOrDomain: String
    ): AvatarsResponse {
        return this.getDecodedResponse<AvatarsResponse>(
            BASE_URL + "get-providers-data-with-avatar",
            mapOf("addressOrDomain" to addressOrDomain)
        )
    }

    suspend fun getDomainForAddresses(
        addresses: List<String>
    ): ResponseBatchV2ReturnTypeDto {
        return this.postDecodedResponse<ResponseBatchV2ReturnTypeDto, List<String>>(
            BASE_URL + "reverse-lookup-batch",
            addresses
        )
    }
}