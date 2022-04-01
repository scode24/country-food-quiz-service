package com.quiz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quiz.entity.model.User;
import com.quiz.jpa.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findUserByUsername(username);
		
		if(user != null) {
			return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getRole());
		}
		
		throw new UsernameNotFoundException(username);
	}
}
