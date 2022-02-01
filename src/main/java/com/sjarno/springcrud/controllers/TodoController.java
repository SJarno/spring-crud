package com.sjarno.springcrud.controllers;

import java.util.List;
import javax.annotation.PostConstruct;

import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/todos")
    public List<Todo> getAllTodos() {
        return this.todoRepository.findAll();
    }
    @GetMapping("/todo/{id}")
    public Todo getTodo(@PathVariable Long id) {
        Todo todo = this.todoRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
        return todo;
    }
    @PostMapping("/add-todo")
    public void addNewTodo(@RequestBody Todo todo) {
        this.todoRepository.save(todo);
    }
    /* Update */

    @DeleteMapping("/delete-todo/{id}")
    public void deleteTodo(@PathVariable Long id) {
        this.todoRepository.deleteById(id);
    }
    @PostConstruct
    public void setUp() {
        this.todoRepository.deleteAll();
        this.todoRepository.save(
            new Todo("Test Title", "Loads of content here"));
    }
    
}
