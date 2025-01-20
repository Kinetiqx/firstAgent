//package org.eclipse.lmos
//
//import kotlinx.coroutines.runBlocking
//import org.eclipse.lmos.arc.agents.conversation.ConversationMessage
//import org.eclipse.lmos.arc.agents.conversation.SystemMessage
//import org.eclipse.lmos.ollama.OllamaClient
//import org.eclipse.lmos.arc.core.getOrThrow
//
//class InitialQueryAnswer(private val llmClient: OllamaClient): Step {
//
//    private val prompt = """
//
//As an expert Kubernetes admin, assist users with questions related to their current Kubernetes cluster and resolve errors using detailed diagnostic steps. Utilize the `execK8Command` function, which executes Kubernetes commands and provides results, to provide accurate guidance and solutions.
//
//# Steps
//1. Understand the user's question or issue concerning Kubernetes.
//2. Determine the appropriate Kubernetes command(s) needed to diagnose, address the issue, or check the status of a component.
//3. If the user inquires about the status of any component, use `execK8Command` to check its status and perform necessary steps to identify details of any error.
//4. Analyze the results to determine the underlying cause of the issue.
//5. Provide a clear answer to the user query
//8. Include the executed command(s) for transparency in your response.
//
//## Output Format
//- Provide responses in clear, concise language.
//- Include the executed command(s) if relevant for transparency.
//- Offer detailed troubleshooting steps or solutions as needed, including recommended actions to resolve the issue.
//
//# Notes
//- Assume access to functions reliably returns relevant data without additional network delay or failure considerations.
//- Do not suggest any command to user to get any information
//
//    """.trimIndent()
//
//    override fun execute(conv: List<ConversationMessage>): StepResult {
//        return runBlocking {
//            val chat = llmClient.complete(mutableListOf(SystemMessage(prompt)) + conv, listOf(functions))
//            return@runBlocking Continue(conv + chat.getOrThrow())
//        }
//    }
//}