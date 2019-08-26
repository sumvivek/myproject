package com.vks.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.vks.app.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
