package com.ping.app

import com.ping.app.data.model.gpt.Message
import com.ping.app.data.repository.chatgpt.ChatGPTRepoImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GptTest {
    
    @Test
    fun ChatGPTTest() = runTest {
        val repo = ChatGPTRepoImpl.initialize()
        val messages = listOf(
            Message(role = "user", content = "오늘 뭐하면 좋을까?")
        )
        runCatching {
            repo.getChatCompletion(messages)
        }.onSuccess {
            println("API 호출 성공: ${it.choices}")
            assertTrue("API 호출이 성공했습니다.", it.choices.isNotEmpty())
        }.onFailure {
            println("API 호출 실패: ${it.message}")
            assertFalse("API 호출이 실패했습니다.", true)
        }
    }
}