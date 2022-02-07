package com.sjarno.springcrud.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjarno.springcrud.models.Todo;
import com.sjarno.springcrud.repositories.TodoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;
    /* Todos with right values: */
    Todo todoOne;
    Todo todoTwo;
    Todo todoThree;

    /* Todos with wrong values: */
    private Todo todoWithNullValues;
    private Todo todoWithoutTitle;
    private Todo todoWithoutContent;

    private List<Todo> todos;

    @BeforeEach
    void setUp() {
        /* Set up right values */
        todoOne = new Todo("Title One", "Content One");
        todoTwo = new Todo("Title Two", "Content Two");
        todoThree = new Todo("Title Three", "Content Three");

        todos = new ArrayList<>();
        todos.add(todoOne);
        todos.add(todoTwo);
        todos.add(todoThree);

        this.todoRepository.saveAll(todos);
        /* Set up wrong values */
        todoWithNullValues = new Todo();
        todoWithoutTitle = new Todo("", "");
        todoWithoutTitle.setContent("content");
        todoWithoutContent = new Todo("", "");
        todoWithoutContent.setTitle("title");

    }

    @Test
    @WithMockUser
    public void authenticatedUserCanGetAllTodos() throws Exception {
        /*
         * More about checking json array:
         * https://stackoverflow.com/questions/55269036/spring-mockmvc-match-a-
         * collection-of-json-objects-in-any-order
         */
        /*
         * Baeldung guide for jsonpath:
         * https://www.baeldung.com/guide-to-jayway-jsonpath
         */

        this.mockMvc.perform(get("/api/todos"))
                .andExpectAll(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[*].id",
                        containsInAnyOrder(1, 2, 3)))
                .andExpect(jsonPath("$[*].title",
                        containsInAnyOrder("Title One", "Title Two", "Title Three")))
                .andExpect(jsonPath("$[*].content",
                        containsInAnyOrder("Content One", "Content Two", "Content Three")));
        checkArraySize();

    }

    @Test
    @WithAnonymousUser
    void anonymousUserCantGetTodosAndRedirectsToLogin() throws Exception {
        this.mockMvc.perform(get("/api/todos"))
                .andExpectAll(status().isFound())
                .andDo(print())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[*].id").doesNotExist())
                .andExpect(jsonPath("$[*].title").doesNotExist())
                .andExpect(jsonPath("$[*].content").doesNotExist());
    }

    @Test
    @WithMockUser
    void testGetTodoById() throws Exception {
        getTodoById(1, "Title One", "Content One");
        getTodoById(2, "Title Two", "Content Two");
        getTodoById(3, "Title Three", "Content Three");

    }

    @Test
    @WithAnonymousUser
    void testGetTodoByIdDoesNotExistAndRedirects() throws Exception {
        this.mockMvc.perform(get("/api/todo/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.title").doesNotExist())
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpectAll(status().isFound());
    }

    @Test
    @WithMockUser
    void todoNotFoundShouldThrowError() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/api/todo/66"))
                .andExpect(status().isNotFound()).andReturn();
        assertEquals("Not found", result.getResponse().getContentAsString());
    }

    

    @Test
    @WithMockUser
    void testAddNewTodo() throws Exception {
        Todo todo = new Todo("title", "content");
        /* Initial size should be 3 */
        checkArraySize();

        MvcResult result = addTodo(todo)
                .andExpect(status()
                .isCreated()).andReturn();
        assertEquals("Success", result.getResponse().getContentAsString());

        checkArraySize(4);
        
    }
    @Test
    @WithAnonymousUser
    void testAnonymousUserCantAddNewTodo() throws Exception {
        Todo todo = new Todo("title", "content");
        MvcResult result = addTodo(todo)
                .andExpect(status()
                .isFound()).andReturn();
        assertEquals("", result.getResponse().getContentAsString());
        
    
    }

    @Test
    @WithMockUser
    void newTodosWithoEmptyValuesDoesNotAddToDb() throws Exception {

        checkArraySize(3);
        valuesReturnsStringWhenAddinTodo(todoWithNullValues, "Values cannot be empty!");

        valuesReturnsStringWhenAddinTodo(todoWithoutTitle, "Title cannot be empty");
        checkArraySize(3);

        valuesReturnsStringWhenAddinTodo(todoWithoutContent, "Content cannot be empty");
        checkArraySize(3);
    }
    @Test
    @WithMockUser
    void canUpdateTodo() throws Exception {
        Todo todoToUpdate = new Todo("title change", "content change");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/api/update/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todoToUpdate)))
                .andExpect(status().isOk()).andReturn();

        assertEquals("Updated", result.getResponse().getContentAsString());
        getTodoById(1, todoToUpdate.getTitle(), todoToUpdate.getContent());
    }

    
    

    @Test
    @WithMockUser
    void wrongValuesDoesNotUpdateTodo() throws Exception {
        Todo todoToUpdate = new Todo("title change", "content change");

        wrongValuesDoNotUpdateTodo("/api/update/91", todoToUpdate, "Not found");
        wrongValuesDoNotUpdateTodo("/api/update/1", todoWithNullValues, "Values cannot be empty!");
        wrongValuesDoNotUpdateTodo("/api/update/1", todoWithoutTitle, "Title cannot be empty");
        wrongValuesDoNotUpdateTodo("/api/update/1", todoWithoutContent, "Content cannot be empty");

    }
    @Test
    @WithMockUser
    void testDeleteTodo() throws Exception {
        checkArraySize(3);
        deleteTodo(3);
        checkArraySize(2);
        deleteTodo(2);
        checkArraySize(1);
        deleteTodo(1);
        checkArraySize(0);

    }

    @Test
    @WithMockUser
    void idNotFoundWhenDeleting() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete-todo/88")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnprocessableEntity()).andReturn();
        assertEquals("Not found", result.getResponse().getContentAsString());
    }

    private ResultActions checkArraySize(int size) throws Exception {
        return this.mockMvc.perform(get("/api/todos")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(jsonPath("$", hasSize(size)));
    }

    private void checkArraySize() throws Exception {
        checkArraySize(this.todos.size());
        assertEquals(this.todos.size(), this.todoRepository.findAll().size());
    }

    private ResultActions addTodo(Todo todo) throws Exception {
        return this.mockMvc.perform(post("/api/add-todo")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)));

    }

    private ResultActions deleteTodo(int id) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/delete-todo/{id}", id)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    private ResultActions getTodoById(int id, String title, String content) throws Exception {
        return this.mockMvc.perform(get("/api/todo/" + id)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.content", is(content)))
                .andExpectAll(status().isFound());
    }

    private void valuesReturnsStringWhenAddinTodo(Todo todo, String errorMessage) throws Exception {
        MvcResult result = this.addTodo(todo).andExpect(status().isUnprocessableEntity()).andReturn();
        assertEquals(errorMessage, result.getResponse().getContentAsString());
    }

    private void wrongValuesDoNotUpdateTodo(String url, Todo todo, String errorMessage) throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put(url)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isUnprocessableEntity()).andReturn();
        assertEquals(errorMessage, result.getResponse().getContentAsString());
    }

}
