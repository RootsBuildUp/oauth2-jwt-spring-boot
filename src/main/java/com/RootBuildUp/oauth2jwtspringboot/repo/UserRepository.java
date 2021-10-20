package com.RootBuildUp.oauth2jwtspringboot.repo;

import com.RootBuildUp.oauth2jwtspringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username,String password);
}
