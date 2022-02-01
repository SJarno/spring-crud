package com.sjarno.springcrud.services;

import java.util.List;

import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Transactional
    public void addTodo(Todo todo) {
        if (todo.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (todo.getContent().isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        todoRepository.save(todo);
    }

    public List<Todo> getAllTodos() {
        return this.todoRepository.findAll();
    }
    
}
