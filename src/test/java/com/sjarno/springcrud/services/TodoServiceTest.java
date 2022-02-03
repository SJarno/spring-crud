package com.sjarno.springcrud.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sjarno.springcrud.models.Todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    private Todo todoWithProperValues;
    private Todo todoNull;
    private Todo todoWithoutTitle;
    private Todo todoWithoutContent;

    
    private Todo dummyTodo;

    @BeforeEach
    void setUp() {
        todoWithProperValues = new Todo("title", "content");
        todoNull = new Todo();
        todoWithoutTitle = new Todo("", "");
        todoWithoutTitle.setContent("content");
        todoWithoutContent = new Todo("", "");
        todoWithoutContent.setTitle("title");

        
        dummyTodo = new Todo("DummyTitle", "DummyContent");
        this.todoService.addTodo(dummyTodo);
    }
    @Test
    public void testAddTodo() {
        assertEquals(1, todoService.getAllTodos().size());
        todoService.addTodo(todoWithProperValues);
        assertEquals(2, todoService.getAllTodos().size());
    }
    @Test
    void emptyValuesThrowsErrorWhenAddingNewTodo() {
        assertEquals(1, todoService.getAllTodos().size());
        Exception nullException = assertThrows(NullPointerException.class, () -> {
            this.todoService.addTodo(todoNull);
        });
        
        assertEquals("Values cannot be empty!", nullException.getMessage());
        assertEquals(1, todoService.getAllTodos().size());

        Exception exceptionWithOutTitle = assertThrows(IllegalArgumentException.class, () -> {
            this.todoService.addTodo(todoWithoutTitle);
            
        });
        assertEquals("Title cannot be empty", exceptionWithOutTitle.getMessage());
        assertEquals(1, todoService.getAllTodos().size());
        
        Exception exceptionWithOutContent = assertThrows(IllegalArgumentException.class, () -> {
            this.todoService.addTodo(todoWithoutContent);
            
        });
        assertEquals("Content cannot be empty", exceptionWithOutContent.getMessage());
        assertEquals(1, todoService.getAllTodos().size());
    }

    @Test
    void testGetAllUsers() {
        assertEquals(1, todoService.getAllTodos().size());
    }

    @Test
    void getTodoById() throws Exception {
        Todo found = this.todoService.findTodoById(1L);
        assertEquals(this.dummyTodo, found);
        assertTrue(found.equals(this.dummyTodo));
        assertNotEquals(this.todoNull, found);
    }
    @Test
    void wrongIdThrowsException() {
        Exception exception = assertThrows(Exception.class, () -> {
            Todo found = this.todoService.findTodoById(99L);
        });
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void testUpdatesSuccessfully() throws Exception {
        assertEquals(1, todoService.getAllTodos().size());

        Todo todoUpdatedTodo = new Todo("Updated Title", "Updated Content");
        this.todoService.updateTodo(todoUpdatedTodo, 1L);
        assertEquals(1, todoService.getAllTodos().size());

        Todo foundTodo = this.todoService.findTodoById(1L);
        assertEquals(todoUpdatedTodo, foundTodo);
        assertNotEquals(dummyTodo, foundTodo);

        assertEquals(1, todoService.getAllTodos().size());
    }

    @Test
    void empytValuesDoesNotUpdateTodo() {
        Exception nullException = assertThrows(NullPointerException.class, () -> {
            this.todoService.updateTodo(todoNull, 1L);
        });
        assertEquals("Values cannot be empty!", nullException.getMessage());

        Exception exceptionWithOutTitle = assertThrows(IllegalArgumentException.class, () -> {
            this.todoService.updateTodo(todoWithoutTitle, 1L);
            
        });
        assertEquals("Title cannot be empty", exceptionWithOutTitle.getMessage());

        Exception exceptionWithOutContent = assertThrows(IllegalArgumentException.class, () -> {
            this.todoService.updateTodo(todoWithoutContent, 1L);
            
        });
        assertEquals("Content cannot be empty", exceptionWithOutContent.getMessage());
    }

    @Test
    void testDeletingWorks() throws Exception {
        assertEquals(1, this.todoService.getAllTodos().size());
        this.todoService.deleteTodoById(1L);
        assertEquals(0, this.todoService.getAllTodos().size());
    }

}
