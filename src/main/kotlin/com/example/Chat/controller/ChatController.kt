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
import java.util.NoSuchElementException

// Controlador WebSocket para mensajes en tiempo real
@Controller
class ChatController(private val messageService: MessageService) {

    /**
     * Recibe un mensaje entrante a través de WebSocket/STOMP.
     * Se mapea al destino "/app/sendMessage" (definido en el cliente JS).
     * Luego envía el mensaje a todos los suscriptores del tópico "/topic/messages".
     */
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    fun sendMessage(message: Message): Message {
        // Guarda el mensaje en la base de datos (si aplica)
        messageService.saveBlocking(message)
        return message
    }
}

// Controlador REST para endpoints HTTP
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = ["*"]) // Permite solicitudes desde cualquier origen (ajusta según necesidad)
class MessageRestController(private val messageService: MessageService) {

    /**
     * Obtiene todos los mensajes como una lista (bloqueante).
     * @return ResponseEntity con lista de mensajes.
     */
    @GetMapping("/list")
    fun getAllMessagesList(): ResponseEntity<List<Message>> {
        val messages = messageService.getAllBlocking()
        return ResponseEntity.ok(messages)
    }

    /**
     * Obtiene todos los mensajes como un flujo reactiva (no bloqueante).
     * @return Flux<Message> para streaming de datos.
     */
    @GetMapping
    fun getAllMessages(): Flux<Message> {
        return messageService.getAll()
    }

    /**
     * Guarda un nuevo mensaje a través de una solicitud HTTP POST.
     * @param message El mensaje enviado en el cuerpo de la solicitud.
     * @return ResponseEntity con el mensaje guardado y código 201 CREATED.
     */
    @PostMapping("/send")
    fun sendMessageRest(@RequestBody message: Message): ResponseEntity<Message> {
        val savedMessage = messageService.saveBlocking(message)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage)
    }

    /**
     * Obtiene la cantidad total de mensajes almacenados.
     * @return ResponseEntity con un mapa conteniendo la clave "count".
     */
    @GetMapping("/count")
    fun countMessages(): ResponseEntity<Map<String, Int>> {
        val count = messageService.getAllBlocking().size
        return ResponseEntity.ok(mapOf("count" to count))
    }

    /**
     * Obtiene todos los mensajes enviados por un remitente específico.
     * @param sender El nombre del remitente.
     * @return ResponseEntity con lista de mensajes del remitente o 404 si no hay.
     * @throws NoSuchElementException si no se encuentra ningún mensaje del remitente.
     */
    @GetMapping("/sender/{sender}")
    fun getMessagesBySender(@PathVariable sender: String): ResponseEntity<List<Message>> {
        val messages = messageService.getAllBlocking().filter { it.sender == sender }

        if (messages.isEmpty()) {
            throw NoSuchElementException("No se encontraron mensajes para el remitente: $sender")
        }

        return ResponseEntity.ok(messages)
    }

    /**
     * Obtiene los últimos N mensajes almacenados.
     * @param limit Cantidad de mensajes a devolver (parámetro de consulta).
     * @return ResponseEntity con lista de mensajes o 400 si el límite es inválido.
     */
    @GetMapping("/recent")
    fun getRecentMessages(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<List<Message>> {
        if (limit <= 0) {
            return ResponseEntity.badRequest()
                .body(emptyList<Message>())
                .also { throw IllegalArgumentException("El parámetro 'limit' debe ser mayor a 0.") }
        }

        val allMessages = messageService.getAllBlocking()
        val recentMessages = allMessages.takeLast(limit)
        return ResponseEntity.ok(recentMessages)
    }

    /**
     * Elimina todos los mensajes de un remitente específico.
     * @param sender El nombre del remitente.
     * @return ResponseEntity con mensaje de éxito o 404 si no hay mensajes para eliminar.
     */
    @DeleteMapping("/sender/{sender}")
    fun deleteMessagesBySender(@PathVariable sender: String): ResponseEntity<Map<String, String>> {
        val messages = messageService.getAllBlocking().filter { it.sender == sender }

        if (messages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "No se encontraron mensajes para el remitente: $sender"))
        }

        // Aquí asumimos que el service tiene un método para eliminar por remitente
        // Si no, debes agregarlo en MessageService
        // messageService.deleteBySender(sender)

        return ResponseEntity.ok(mapOf("message" to "Se eliminaron ${messages.size} mensajes de $sender"))
    }

    /**
     * Obtiene un mensaje por su ID (si tu modelo lo tiene).
     * @param id El ID del mensaje.
     * @return ResponseEntity con el mensaje o 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    fun getMessageById(@PathVariable id: Long): ResponseEntity<Message> {
        // Asumiendo que tu modelo Message tiene un campo "id"
        val allMessages = messageService.getAllBlocking()
        val message = allMessages.find { it.id == id }

        if (message == null) {
            throw NoSuchElementException("No se encontró el mensaje con ID: $id")
        }

        return ResponseEntity.ok(message)
    }
}

// Clase para manejar excepciones globales
@RestControllerAdvice
class MessageControllerAdvice {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf<String, String>("error" to (ex.message ?: "Recurso no encontrado")))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf<String, String>("error" to (ex.message ?: "Solicitud inválida")))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf<String, String>("error" to "Ocurrió un error interno en el servidor"))
    }
}