package com.sjarno.springcrud.repositories;

import com.sjarno.springcrud.models.Todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>{

}
