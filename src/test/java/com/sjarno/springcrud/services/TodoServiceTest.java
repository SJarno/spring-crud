package com.sjarno.springcrud.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    
    @Test
    public void testAddTodo() {
        assertEquals(1, todoService.getAllTodos().size());
        Todo todo = new Todo("title", "content");
        todoService.addTodo(todo);
        assertEquals(2, todoService.getAllTodos().size());
    }
    @Test
    void emptyValuesThrowsError() {
        Todo todoWithoutTitle = new Todo();
        todoWithoutTitle.setContent("content");
        Todo todoWithoutContent = new Todo();
        assertEquals(1, todoService.getAllTodos().size());
        Exception exception = assertThrows(Exception.class, () -> {
            this.todoService.addTodo(todoWithoutTitle);
            this.todoService.addTodo(todoWithoutContent);
        });
        assertEquals(1, todoService.getAllTodos().size());
        //assertEquals("expected", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        assertEquals(1, todoService.getAllTodos().size());
    }

}
