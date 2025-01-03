package org.eclipse.lmos

import kotlinx.coroutines.runBlocking
import org.eclipse.lmos.arc.agents.conversation.ConversationMessage
import org.eclipse.lmos.arc.agents.conversation.SystemMessage
import org.eclipse.lmos.arc.client.ollama.OllamaClient
import org.eclipse.lmos.arc.core.getOrThrow

class MoreInfoRequired(private val llmClient: OllamaClient): Step {

    private val prompt = """
        evaluate if the user query if answered completely based on the conversation.
                        Reply "YES" if query is answered
                        Reply "NO" if not answered
                        
                        #Note - Reply with single word "YES" or "NO"              
    """.trimIndent()

    override fun execute(conv: List<ConversationMessage>): StepResult {
        return runBlocking {
            val chat = llmClient.complete(mutableListOf(SystemMessage(prompt)) + conv)
            val assistantMessage = chat.getOrThrow()
            if(assistantMessage.content == "NO") {
                return@runBlocking Loop(conv + chat.getOrThrow(), AdditionalCommand(llmClient))
            } else {
                return@runBlocking Continue(conv + chat.getOrThrow())
            }
        }
    }
}