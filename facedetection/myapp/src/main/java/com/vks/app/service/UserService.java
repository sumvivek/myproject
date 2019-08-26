package com.vks.app.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.vks.app.model.User;
import com.vks.app.repository.UserRepository;

@Service
@Transactional
public class UserService {
	private UserRepository userRepository;
	public UserService(UserRepository userRepository)
	{
		this.userRepository=userRepository;
	}
 public void saveMyUser(User user)
 {
	 userRepository.save(user);
 }
}
