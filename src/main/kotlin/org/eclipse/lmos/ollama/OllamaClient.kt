// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.ollama

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.eclipse.lmos.ollama.*
import org.slf4j.LoggerFactory

/**
 * Calls a Ollama Chat endpoint to complete a conversation.
 */
class OllamaClient(
    private val languageModel: OllamaClientConfig,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient(CIO) {
        install(DefaultRequest) {
            url(languageModel.url ?: "http://localhost:11434")
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    log.debug(message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun complete(
        messages: List<ChatMessage>,
//        functions: List<LLMFunction>?,
//        settings: ChatCompletionSettings?,
    ) = runBlocking {
//        val ollamaMessages = toOllamaMessages(messages)

//        val ollamaTools = languageModel.toolSupported.takeIf { it }?.let { toOllamaTools(functions) } ?: emptyList()

//        val functionCallHandler = FunctionCallHandler(functions ?: emptyList(), eventHandler)

//        eventHandler?.publish(LLMStartedEvent(languageModel.modelName))
//        val result: Result<ChatResponse, ArcException>
//        val duration = measureTime {
            val result = chat(messages)
        return@runBlocking result
//        , ollamaTools, functionCallHandler, settings)
//        }
//        var chatResponse: ChatResponse? = null
//        finally { publishEvent(it, messages, functions, chatResponse, duration, functionCallHandler, settings) }
//        chatResponse = result failWith { it }
//        chatResponse.getFirstAssistantMessage(
//            sensitive = functionCallHandler.calledSensitiveFunction(),
//            settings = settings,
//        )
    }

//    private fun toOllamaTools(
//        functions: List<LLMFunction>?,
//        parametersSchema: org.eclipse.lmos.arc.agents.functions.ParametersSchema? = null,
//    ): List<Tool>? {
//        return functions?.map { llmFunction ->
//            Tool(
//                type = "function",
//                function = Function(
//                    name = llmFunction.name,
//                    description = llmFunction.description,
//                    parameters = parametersSchema?.let { schema ->
//                        Parameters(
//                            type = "object",
//                            properties = schema.parameters.associate {
//                                it.name to Property(
//                                    type = it.type.schemaType,
//                                    description = it.description,
//                                    enum = it.enum,
//                                )
//                            },
//                            required = schema.required,
//                        )
//                    },
//                ),
//            )
//        }
//    }

    private suspend fun chat(messages: List<ChatMessage>, tools: List<Tool>? = null) = runBlocking {
        val response: HttpResponse = client.post("${languageModel.url}/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(
                ChatRequest(
                    languageModel.modelName,
                    messages,
                    stream = false,
                    format = null,
                    tools = tools,
                ),
            )
        }.body()

        val chatResponse = response.takeIf { it.status.isSuccess() }
            ?.let { json.decodeFromString<ChatResponse>(it.bodyAsText()) }
            ?: throw RuntimeException(response.bodyAsText())

//        val newMessages: List<ChatMessage> = functionCallHandler.handle(chatResponse).getOrThrow()
//        if (newMessages.isNotEmpty()) {
//            return chat(messages + newMessages, tools, functionCallHandler, settings)
//        }
        return@runBlocking chatResponse
    }

//    private fun toOllamaMessages(messages: List<ConversationMessage>) =
//        messages.map { msg ->
//            when (msg) {
//                is SystemMessage -> ChatMessage(
//                    content = msg.content,
//                    role = "system",
//                )
//                is AssistantMessage -> ChatMessage(
//                    content = msg.content,
//                    role = "assistant",
//                )
//                is UserMessage -> if (msg.binaryData.isNotEmpty()) {
//                    ChatMessage(
//                        content = msg.content,
//                        role = "user",
//                        images = msg.binaryData.map { it.data.encodeBase64() },
//                    )
//                } else {
//                    ChatMessage(content = msg.content, role = "user")
//                }
//                else -> throw ArcException("Unsupported message type: $msg")
//            }
//        }

//    private fun ChatResponse.getFirstAssistantMessage(
//        sensitive: Boolean = false,
//        settings: ChatCompletionSettings?,
//    ) = AssistantMessage(
//        message.content,
//        sensitive = sensitive,
//        format = when (settings?.format) {
//            JSON -> MessageFormat.JSON
//            else -> MessageFormat.TEXT
//        },
//    )
}
