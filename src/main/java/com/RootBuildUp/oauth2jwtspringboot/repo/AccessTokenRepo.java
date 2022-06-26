package com.RootBuildUp.oauth2jwtspringboot.repo;

import com.RootBuildUp.oauth2jwtspringboot.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepo extends JpaRepository<AccessToken, String>{

}

