package com.learning.microservices.UsersMicroservice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
}
