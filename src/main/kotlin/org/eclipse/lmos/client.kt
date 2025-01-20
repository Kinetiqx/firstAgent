//package org.eclipse.lmos
//
//import com.openai.client.okhttp.OpenAIOkHttpClient
//import com.openai.models.ChatCompletionCreateParams
//import com.openai.models.ChatCompletionMessageParam
//import com.openai.models.ChatCompletionUserMessageParam
//import com.openai.models.ChatModel
//import kotlinx.coroutines.runBlocking
//import java.util.*
//
//class OpenAIClient {
//
//    fun chat(query: String): Optional<String> = runBlocking {
//
//        val client = OpenAIOkHttpClient.builder()
//            .apiKey("dummKey")
//            .build();
//
//        val params = ChatCompletionCreateParams.builder()
//            .messages(
//                listOf(
//                    ChatCompletionMessageParam.ofChatCompletionUserMessageParam(
//                        ChatCompletionUserMessageParam.builder()
//                            .role(ChatCompletionUserMessageParam.Role.USER)
//                            .content(ChatCompletionUserMessageParam.Content.ofTextContent(query))
//                            .build()
//                    )
//                )
//            )
//            .model(ChatModel.GPT_4O_MINI)
//            .build()
//        val chatCompletion = client.chat().completions().create(params)
//
//        return@runBlocking chatCompletion.choices()[0].message().content()
//
//    }
//}