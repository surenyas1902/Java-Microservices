package com.learning.microservices.UsersMicroservice.service.impl;

import com.learning.microservices.UsersMicroservice.data.UserEntity;
import com.learning.microservices.UsersMicroservice.data.UsersRepository;
import com.learning.microservices.UsersMicroservice.service.api.UsersService;
import com.learning.microservices.UsersMicroservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        // ModelMapper mapper = new ModelMapper();
        // mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity entity = mapper.map(userDetails, UserEntity.class);
        usersRepository.save(entity);

        UserDto returnValue = mapper.map(entity, UserDto.class);
        return returnValue;
    }
}
