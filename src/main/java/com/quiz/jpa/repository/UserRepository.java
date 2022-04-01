package com.quiz.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.quiz.entity.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from User u where u.username = :username")
	public User findUserByUsername(String username);
}
