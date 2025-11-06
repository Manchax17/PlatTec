package com.example.chat.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("messages")
data class Message(
    @Id val id: Long? = null,
    val content: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis()
)
