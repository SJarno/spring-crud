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
        // Validate todo values
        if (validateTodo(todo));
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

    @Transactional
    public void updateTodo(Todo todo, Long id) throws Exception {
        Todo foundTodo = findTodoById(id);

        if (validateTodo(todo));
        foundTodo.setTitle(todo.getTitle());
        foundTodo.setContent(todo.getContent());
        this.todoRepository.save(foundTodo);

    }

    private boolean validateTodo(Todo todo) {
        if (todo.getTitle() == null || todo.getContent() == null) {
            throw new NullPointerException("Values cannot be empty!");
        }
        if (todo.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (todo.getContent().isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        return true;
    }

    public void deleteTodoById(Long id) throws Exception {
        Todo todoToDelete = this.findTodoById(id);
        this.todoRepository.delete(todoToDelete);
    }

}
