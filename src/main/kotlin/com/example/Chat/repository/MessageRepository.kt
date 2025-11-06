package com.example.chat.repository

import com.example.chat.model.Message
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : R2dbcRepository<Message, Long>
