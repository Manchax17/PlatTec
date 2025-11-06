package com.example.chat.controller

import com.example.chat.model.Message
import com.example.chat.service.MessageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

// Controlador WebSocket
@Controller
class ChatController(private val messageService: MessageService) {

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    fun sendMessage(message: Message): Message {
        messageService.saveBlocking(message)
        return message
    }
}

// Controlador REST (endpoints HTTP)
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = ["*"])
class MessageRestController(private val messageService: MessageService) {

    @GetMapping("/list")
    fun getAllMessagesList(): ResponseEntity<List<Message>> {
        val messages = messageService.getAllBlocking()
        return ResponseEntity.ok(messages)
    }

    @GetMapping
    fun getAllMessages(): Flux<Message> {
        return messageService.getAll()
    }

    @PostMapping("/send")
    fun sendMessageRest(@RequestBody message: Message): ResponseEntity<Message> {
        val savedMessage = messageService.saveBlocking(message)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage)
    }   

    @GetMapping("/count")
    fun countMessages(): ResponseEntity<Map<String, Int>> {
        val count = messageService.getAllBlocking().size
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @GetMapping("/sender/{sender}")
    fun getMessagesBySender(@PathVariable sender: String): ResponseEntity<List<Message>> {
        val messages = messageService.getAllBlocking().filter { it.sender == sender }
        return ResponseEntity.ok(messages)
    }
}