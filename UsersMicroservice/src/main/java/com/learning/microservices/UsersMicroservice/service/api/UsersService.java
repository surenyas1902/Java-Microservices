package com.learning.microservices.UsersMicroservice.service.api;

import com.learning.microservices.UsersMicroservice.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDetails);

    UserDto getUserByEmail(String email);
}
