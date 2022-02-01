package com.sjarno.springcrud.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjarno.springcrud.models.Todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testGetAllTodos() throws Exception {
        /*
         * More about checking json array:
         * https://stackoverflow.com/questions/55269036/spring-mockmvc-match-a-
         * collection-of-json-objects-in-any-order
         */
        /*
         * Baeldung guide for jsonpath:
         * https://www.baeldung.com/guide-to-jayway-jsonpath
         */
        checkArraySize(1);
        this.mockMvc.perform(get("/api/todos"))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Test Title")))
                .andExpect(jsonPath("$[*].content", containsInAnyOrder("Loads of content here")));
        // array of items
        checkArraySize(1);
        // array of members
        /*
         * mockMvc.perform(get("/api/todos"))
         * .andExpect(jsonPath("$.*", hasSize(2)));
         */
    }

    @Test
    void testGetTodo() throws Exception {
        this.mockMvc.perform(get("/api/todo/1"))
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Title")))
                .andExpect(jsonPath("$.content", is("Loads of content here")));

    }

    @Test
    void testAddNewTodo() throws Exception {
        Todo todo = new Todo("title", "content");
        /* Initial size should be 1 */
        checkArraySize(1);

        addTodo(todo);
        /* Should be 2 */
        checkArraySize(2);
    }

    @Test
    void testDeleteTodo() throws Exception {
        checkArraySize(1);
        deleteTodo(1);
        checkArraySize(0);
        addTodo(new Todo("title", "content"));
        addTodo(new Todo("title", "content"));
        addTodo(new Todo("title", "content"));
        addTodo(new Todo("title", "content"));
        checkArraySize(4);
        deleteTodo(2);
        checkArraySize(3);
        deleteTodo(3);
        checkArraySize(2);
        deleteTodo(4);
        checkArraySize(1);
        deleteTodo(5);
        checkArraySize(0);
        

    }

    private ResultActions checkArraySize(int size) throws Exception {
        return this.mockMvc.perform(get("/api/todos"))
                .andExpect(jsonPath("$", hasSize(size)));
    }

    private ResultActions addTodo(Todo todo) throws Exception {
        return this.mockMvc.perform(post("/api/add-todo")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }
    private ResultActions deleteTodo(int id) throws Exception{
        return this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete-todo/{id}", id))
                .andExpect(status().isOk());
    }

}
