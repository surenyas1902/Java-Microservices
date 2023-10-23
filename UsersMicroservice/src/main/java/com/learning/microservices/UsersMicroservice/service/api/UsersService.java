package com.learning.microservices.UsersMicroservice.service.api;

import com.learning.microservices.UsersMicroservice.shared.UserDto;

public interface UsersService {

    UserDto createUser(UserDto userDetails);
}
