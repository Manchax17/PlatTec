package com.example.chat.service

import com.example.chat.model.Message
import com.example.chat.repository.MessageRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

@Service
class MessageService(private val repository: MessageRepository) {
    fun save(message: Message): Mono<Message> = repository.save(message)

    // Método bloqueante para usar con WebSocket
    fun saveBlocking(message: Message): Message = repository.save(message).block()!!

    fun getAll(): Flux<Message> = repository.findAll()

    // Método bloqueante para obtener todos los mensajes
    fun getAllBlocking(): List<Message> = repository.findAll().collectList().block() ?: emptyList()
}