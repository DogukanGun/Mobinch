package com.dag.one_inch.tools.swap

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage

interface FusionOrdersAgent {

    @SystemMessage("You are a helpful assistant for interacting with the 1inch Fusion+ API.")
    @UserMessage("{{userMessage}}")
    fun chat(userMessage: String): String
}