package com.example.todoapi.service;

import com.example.todoapi.web.v1.model.UserDto;
import lombok.NonNull;

public interface AuthenticationService {

    UserDto authenticate(@NonNull String username, @NonNull String password);
}
