package com.example.chat.repository

import com.example.chat.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveCrudRepository<User, Long>
