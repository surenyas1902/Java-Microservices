package com.learning.microservices.UsersMicroservice.controller;

import com.learning.microservices.UsersMicroservice.model.CreateUserRequest;
import com.learning.microservices.UsersMicroservice.model.CreateUserResponse;
import com.learning.microservices.UsersMicroservice.service.api.UsersService;
import com.learning.microservices.UsersMicroservice.shared.UserDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment env;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/status/check")
    public String status() {
        return "Working on " + env.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto userDto = mapper.map(request, UserDto.class);
        UserDto createdUser = usersService.createUser(userDto);
        CreateUserResponse response = mapper.map(createdUser, CreateUserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
