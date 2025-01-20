//package org.eclipse.lmos
//
//import kotlinx.coroutines.runBlocking
//import org.eclipse.lmos.arc.agents.conversation.ConversationMessage
//import org.eclipse.lmos.arc.agents.conversation.SystemMessage
//import org.eclipse.lmos.ollama.OllamaClient
//import org.eclipse.lmos.arc.core.getOrThrow
//
//class AdditionalCommand(private val llmClient: OllamaClient): Step {
//
//    private val prompt = """
//use execK8Command function to get additional information required to answer user query
//    """.trimIndent()
//
//    override fun execute(conv: List<ConversationMessage>): StepResult {
//        return runBlocking {
//            val chat = llmClient.complete(mutableListOf(SystemMessage(prompt)) + conv, listOf(functions))
//            return@runBlocking Loop(conv + chat.getOrThrow(), MoreInfoRequired(llmClient))
//        }
//    }
//}