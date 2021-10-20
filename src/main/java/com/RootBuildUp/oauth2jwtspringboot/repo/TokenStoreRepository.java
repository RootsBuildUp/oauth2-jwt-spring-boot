package com.RootBuildUp.oauth2jwtspringboot.repo;

import com.RootBuildUp.oauth2jwtspringboot.model.TokenStore;
import com.RootBuildUp.oauth2jwtspringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenStoreRepository extends JpaRepository<TokenStore,Integer> {
   TokenStore getTokenStoreByUser(User user);
   TokenStore getTokenStoreByTokenStore(String tokenStore);
}
