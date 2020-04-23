package com.example.todoapi.repository;

import com.example.todoapi.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
}
