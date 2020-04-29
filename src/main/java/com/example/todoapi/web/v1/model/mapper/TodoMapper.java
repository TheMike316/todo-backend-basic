package com.example.todoapi.web.v1.model.mapper;

import com.example.todoapi.domain.Todo;
import com.example.todoapi.web.v1.model.TodoDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(TodoMapperDecorator.class)
public interface TodoMapper {

    TodoDto todoToTodoDto(Todo todo);

    Todo todoDtoToTodo(TodoDto dto);
}
