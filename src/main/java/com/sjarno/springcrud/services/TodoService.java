package com.sjarno.springcrud.services;

import java.util.List;
import java.util.Optional;


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
        /* Check for null values: */
        if (todo.getTitle() == null || todo.getContent() == null) {
            throw new NullPointerException("Values cannot be empty!");
        }
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

    public Todo findTodoById(Long id) throws Exception {
        Optional<Todo> foundTodo = this.todoRepository.findById(id);
        if (foundTodo.isPresent()) {
            return foundTodo.get();
        }
        throw new Exception("Not found");

    }
    
}
