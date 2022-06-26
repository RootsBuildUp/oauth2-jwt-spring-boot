package com.RootBuildUp.oauth2jwtspringboot.redis.service;


import com.service.cblmodel.redis.model.OAuthToken;
import com.service.cblmodel.redis.repo.OAuthTokenRepo;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class OAthTokenRepoImpl implements OAuthTokenRepo {
	private RedisTemplate<String, OAuthToken> redisTemplate;
	private HashOperations hashOperations;
	private String redisObjectName = "OAuthToken";

	public OAthTokenRepoImpl(RedisTemplate<String, OAuthToken> redisTemplate) {
		this.redisTemplate = redisTemplate;
		hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public void save(OAuthToken oAuthToken) {
		System.out.println(oAuthToken.getExpiredDateAndTime());
		hashOperations.put(redisObjectName,oAuthToken.getId() , oAuthToken);
	}

	@Override
	public Map<String, OAuthToken> findAll() {
		return hashOperations.entries(redisObjectName);
	}

	@Override
	public OAuthToken findById(String id) {
		return (OAuthToken) hashOperations.get(redisObjectName, id);
	}

	@Override
	public void update(OAuthToken oAuthToken) {
		save(oAuthToken);
	}

	@Override
	public void delete(String id) {
		hashOperations.delete(redisObjectName, id);
	}
}
