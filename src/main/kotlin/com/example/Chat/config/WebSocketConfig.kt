package com.example.chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Habilita el broker para temas (prefix /topic)
        config.enableSimpleBroker("/topic")
        // Prefijo de aplicación para los mensajes enviados desde el cliente
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Define el endpoint principal del WebSocket
        registry.addEndpoint("/chat")
            .setAllowedOriginPatterns("*")
            .withSockJS()  // soporte para clientes sin WebSocket nativo
    }
}