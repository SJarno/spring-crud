package com.sjarno.springcrud.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjarno.springcrud.models.Todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    

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
        // array of items
        this.mockMvc.perform(get("/api/todos"))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Test Title")))
                .andExpect(jsonPath("$[*].content", containsInAnyOrder("Loads of content here")));

        checkArraySize(1);
        // array of members:
        /*
         * mockMvc.perform(get("/api/todos"))
         * .andExpect(jsonPath("$.*", hasSize(2)));
         */
    }

    @Test
    void testGetTodoById() throws Exception {
            getTodoById(1, "Test Title", "Loads of content here");

    }

    @Test
    void todoNotFoundShouldThrowError() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/api/todo/66"))
            .andExpect(status().isNotFound()).andReturn();
        assertEquals("Not found", result.getResponse().getContentAsString());
    }

    @Test
    void canUpdateTodo() throws Exception {
        Todo todoToUpdate = new Todo("title change", "content change");
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/update/1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(todoToUpdate)))
            .andExpect(status().isOk());

        getTodoById(1, todoToUpdate.getTitle(), todoToUpdate.getContent());
    }

    @Test
    void testAddNewTodo() throws Exception {
        Todo todo = new Todo("title", "content");
        /* Initial size should be 1 */
        checkArraySize(1);

        MvcResult result = addTodo(todo).andExpect(status()
            .isCreated()).andReturn();
        assertEquals("Success", result.getResponse().getContentAsString());
        /* Should be 2 */
        checkArraySize(2);
    }
    @Test
    void newTodosWithoEmptyValuesDoesNotAddToDb() throws Exception {
        Todo todoWithoutTitle = new Todo();
        todoWithoutTitle.setContent("content");
        Todo todoWithoutContent = new Todo();
        todoWithoutContent.setTitle("title");
        checkArraySize(1);
        addTodo(todoWithoutTitle)
            .andExpect(status().isUnprocessableEntity());

        checkArraySize(1);

        addTodo(todoWithoutContent)
            .andExpect(status().isUnprocessableEntity());

        checkArraySize(1);
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
                .content(objectMapper.writeValueAsString(todo)));
                //.andExpect(status().isCreated());
    }

    private ResultActions deleteTodo(int id) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete-todo/{id}", id))
                .andExpect(status().isOk());
    }

    private ResultActions getTodoById(int id, String title, String content) throws Exception {
        return this.mockMvc.perform(get("/api/todo/"+id)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.content", is(content)))
                .andExpectAll(status().isFound());
    }

}
