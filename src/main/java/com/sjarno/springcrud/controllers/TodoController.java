package com.sjarno.springcrud.controllers;

import java.util.List;

import javax.annotation.PostConstruct;

import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;
import com.sjarno.springcrud.services.TodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @GetMapping("/todos")
    public List<Todo> getAllTodos() {
        return this.todoService.getAllTodos();
    }
    @GetMapping("/todo/{id}")
    public ResponseEntity<?> getTodo(@PathVariable Long id) throws Exception{
        try {
            return new ResponseEntity<Todo>(this.todoService.findTodoById(id), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/add-todo")
    public ResponseEntity<?> addNewTodo(@RequestBody Todo todo) {
        try {
            this.todoService.addTodo(todo);
            return new ResponseEntity<String>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
    }
    /* Update */
    @PutMapping("/update/{todoId}")
    public ResponseEntity<String> updateTodo(@RequestBody Todo todo, @PathVariable("todoId") Long id) {
        try {
            this.todoService.updateTodo(todo, id);
            return new ResponseEntity<>("Updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
    }

    @DeleteMapping("/delete-todo/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        try {
            this.todoService.deleteTodoById(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    /* Initial data here for testing purposes: */
    @PostConstruct
    public void setUp() {
        this.todoRepository.deleteAll();
        this.todoRepository.save(
            new Todo("Test Title", "Loads of content here"));
    }
    
}
