package org.eclipse.lmos

import kotlinx.coroutines.runBlocking
import org.eclipse.lmos.arc.agents.conversation.ConversationMessage
import org.eclipse.lmos.arc.agents.conversation.SystemMessage
import org.eclipse.lmos.arc.client.ollama.OllamaClient
import org.eclipse.lmos.arc.core.getOrThrow

class FinalAnswer(private val llmClient: OllamaClient): Step {

    private val prompt = """
use the conversation history to provide the best possible answer to user query           
    """.trimIndent()

    override fun execute(conv: List<ConversationMessage>): StepResult {
        return runBlocking {
            val chat = llmClient.complete(mutableListOf(SystemMessage(prompt)) + conv)
            return@runBlocking Continue(conv + chat.getOrThrow())
    }
}
    }