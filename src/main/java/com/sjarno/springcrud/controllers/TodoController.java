package com.sjarno.springcrud.controllers;

import java.util.List;
import javax.annotation.PostConstruct;

import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;
import com.sjarno.springcrud.services.TodoService;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
    public Todo getTodo(@PathVariable Long id) throws Exception{
        Todo todo = this.todoRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
        return todo;
    }
    @PostMapping("/add-todo")
    public ResponseEntity<?> addNewTodo(@RequestBody Todo todo) {
        try {
            this.todoService.addTodo(todo);
            return new ResponseEntity<String>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            /* String errorMessage = e.getMessage().toString();
            System.out.println(errorMessage); */
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
    }
    /* Update */
    @Transactional
    @PutMapping("/update/{todoId}")
    public void updateTodo(@RequestBody Todo todo, @PathVariable("todoId") Long id) {
        Todo todoToUpdate = this.todoRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
        if(todo.getContent().isEmpty() || todo.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Arvot eivät saa olla tyhjiä");
        }
        todoToUpdate.setTitle(todo.getTitle());
        todoToUpdate.setContent(todo.getContent());
    }

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
