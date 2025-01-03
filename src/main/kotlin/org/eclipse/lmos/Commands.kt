package org.eclipse.lmos


import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import kotlinx.coroutines.runBlocking
import org.eclipse.lmos.arc.agents.conversation.SystemMessage
import org.eclipse.lmos.arc.agents.conversation.UserMessage
import org.eclipse.lmos.arc.agents.functions.*
import org.eclipse.lmos.arc.client.ollama.OllamaClient
import org.eclipse.lmos.arc.client.ollama.OllamaClientConfig
import org.eclipse.lmos.arc.client.openai.OpenAINativeClient
import org.eclipse.lmos.arc.client.openai.OpenAINativeClientConfig
import org.eclipse.lmos.arc.core.result
import picocli.CommandLine
import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.io.BufferedReader
import java.util.concurrent.Callable
import kotlin.system.exitProcess


val functions = object: LLMFunction {
    override val description: String
        get() = "Executes kubernetes command and returns result"
    override val group: String?
        get() = "K8s"
    override val isSensitive: Boolean
        get() = false
    override val name: String
        get() = "execK8Command"
    override val parameters: ParametersSchema
        get() = ParametersSchema(
            required = listOf("command"),
            parameters = listOf(
                ParameterSchema(
                    name = "command",
                    description = "Kubernetes command",
                    type = ParameterType(schemaType = "string"),
                    enum = listOf()
                )
            )
        )

    override suspend fun execute(input: Map<String, Any?>): org.eclipse.lmos.arc.core.Result<String, LLMFunctionException> = result<String, LLMFunctionException> {
        val command = input["command"].toString()
        println("Executing command: $input")
        // Create and start the process
        val process = ProcessBuilder(command.split(" "))
            .redirectErrorStream(true)
            .start()

        // Capture the output
        val response = process.inputStream.bufferedReader().use(BufferedReader::readText).also {
            process.waitFor() // Wait for the process to complete
        }

        println("Command result: $response")

        response
    }


}

@Command(
    name = "sw",
    version = ["0.1"],
    mixinStandardHelpOptions = true,
    description = ["A Star Wars CLI built on top of https://swapi.dev/"]
)
class SwaCLIOptions: Callable<Int> {
    @Spec
    lateinit var spec: CommandSpec

    @Parameters(index = "0", arity = "0..1", description = ["Search query for the request. (Example : Anakin)"])
    private var searchQuery : String? = null

    override fun call(): Int {
        searchQuery = "what all pods are failing in lmos namespace"
//        searchQuery = promptQuery()
        val ollamaClient = OllamaClient(OllamaClientConfig(modelName = "nexusraven:13b", url = "http://localhost:11434", toolSupported = true))

        val key = "dummy"

        val llmClient = OpenAINativeClient(
            config = OpenAINativeClientConfig("gpt-4o-mini", "https://api.openai.com/v1/chat", key),
            client = OpenAIOkHttpClientAsync.builder()
                .apiKey(key)
                .build(),
            eventHandler = null,
        )

                println("Welcome to ARC-CLI! Type your query and press Enter. Type '/end' to exit.")
                var continueSession = true
                while (continueSession) {
                    print("You: ")
                    val userInput = readlnOrNull()?.trim()
                    if(userInput.isNullOrEmpty()) {
                        println("Empty input")
                    } else if (userInput.equals("/end", ignoreCase = true)) {
                        println("Goodbye!")
                        continueSession = false
                    } else {
//                        processInput(userInput, llmClient)
                        processInput(userInput, ollamaClient)
                    }
                }

//                val stepExecutor = StepExecutor()
//                    .seq()
//                    .step(InitialQueryAnswer(llmClient))
//                    .step(MoreInfoRequired(llmClient))
//                    .step(AdditionalCommand(llmClient))
//                    .step(FinalAnswer(llmClient))
//                    .end()
//
//                stepExecutor.execute(listOf(UserMessage(searchQuery!!)))

//        val openAIClient = OpenAIClient()
//        println("OpenAI Response")
//        println(openAIClient.chat(searchQuery!!).getOrNull())
        return 0
    }


    private fun processInput(query: String, llmClient: OpenAINativeClient) {
        runBlocking {
            println("Executing OpenAINativeClient: $query")
            println(llmClient.complete(
                listOf(SystemMessage(prompt1()), UserMessage(query)),
                functions = listOf(functions),
            ))
        }
    }

    private fun processInput(query: String, llmClient: OllamaClient) {
        runBlocking {
            println("Executing $query")
            println(llmClient.complete(
                listOf(SystemMessage(prompt1()), UserMessage("hi")),
                functions = listOf(functions),
            ))
        }
    }

    private fun prompt1() = """
                            As an expert Kubernetes admin, assist users with questions related to their current Kubernetes cluster.
                            Utilize the `execK8Command` function, which executes Kubernetes commands and provides results.
    
                            # Steps
    
                            1. Understand the user's question or issue concerning Kubernetes.
                            2. Determine the appropriate Kubernetes command(s) needed to diagnose or address the issue.
                            3. Use the `execK8Command` function to execute the identified command(s) and obtain results.
                            4. Analyze the results returned by `execK8Command`.
                            5. Provide a clear response to the user based on the analysis.
    
                            # Output Format
    
                            - Provide responses in clear, concise language.
    
                            # Examples
    
                            **Example 1:**
    
                            - **User Input:** "How can I find the current status of my pods?"
                            - **Execution:** kubectl get po
                            - **Output:** "Current status of your pods: [RESULT FROM COMMAND]. Ensure all pods are running. If some are not, consider checking the logs with execK8Command('kubectl logs [POD_NAME]')."
    
                            **Example 2:**
    
                            - **User Input:** "what all apps are deployed?"
                            - **Execution:** Use execK8Command to get deployment and then again use execK8Command to find all the apps in every deployment
                            - **Output:** "The deployment details show: [RESULT FROM COMMAND]. This might indicate [POSSIBLE ISSUES]. You can try scaling down and up or check the rollout with execK8Command('kubectl rollout status deployment/[DEPLOYMENT_NAME]')."
    
                            # Notes
   
                            - Do not add prefix or suffix to commands
                            - If a command returns an error, provide advice on interpreting or acting on the error messages.
                            - Assume access to functions reliably returns relevant data without additional network delay or failure considerations.
                        """

    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            exitProcess(CommandLine(SwaCLIOptions()).execute(*args))
        }
    }
}