package com.RootBuildUp.oauth2jwtspringboot.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import com.RootBuildUp.oauth2jwtspringboot.model.AccessToken;
import com.RootBuildUp.oauth2jwtspringboot.redis.service.OAthTokenRepoImpl;
import com.RootBuildUp.oauth2jwtspringboot.repo.AccessTokenRepo;
import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import com.service.cblmodel.redis.model.OAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AccessTokenService {
	
	@Autowired
	private AccessTokenRepo accessRepo;
	@Autowired
	private OAthTokenRepoImpl oAthTokenRepo;



	@Async
	public void asyncMethodForTokenTimeUpdate(String tokenId,OAuthToken oAuthToken) {
		AccessToken accessToken = accessRepo.findById(tokenId).get();
		if(accessToken!=null)accessRepo.save(accessToken.setExpiredDateAndTime(accessToken.getExpiredDateAndTime().plusMinutes(VariableName.expiredDate)));
		oAthTokenRepo.save(oAuthToken.setExpiredDateAndTime(oAuthToken.getExpiredDateAndTime().plusMinutes(VariableName.expiredDate)));
	}
	@Async
	public void asyncMethodForTokenStore(OAuth2AccessToken token, OAuth2Authentication authentication) {
			new Thread(()->{
				accessRepo.save(new AccessToken().setId(authentication.getName()+authentication.getOAuth2Request().getClientId())
						.setUsername(authentication.getName())
						.setClientId(authentication.getOAuth2Request().getClientId())
						.setToken(token.getValue())
						.setRefreshToken(null)
						.setAuthentication(null).setExpiredDateAndTime(LocalDateTime.now().plusMinutes(VariableName.expiredDate)));
			}).start();

			new Thread(()->{
				oAthTokenRepo.save(new OAuthToken().setId(authentication.getName()+authentication.getOAuth2Request().getClientId())
						.setUsername(authentication.getName())
						.setClientId(authentication.getOAuth2Request().getClientId())
						.setToken(token.getValue())
						.setRefreshToken(null)
						.setAuthentication(null).setExpiredDateAndTime(LocalDateTime.now().plusMinutes(VariableName.expiredDate))
				);
			}).start();

	}

	public Boolean tokenDataMoveDBToRedis(){
		List<AccessToken> accessTokenList = accessRepo.findAll();
		for (AccessToken accessToken: accessTokenList) {
			oAthTokenRepo.save(new OAuthToken().setId(accessToken.getId())
					.setUsername(accessToken.getUsername())
					.setClientId(accessToken.getClientId())
					.setToken(accessToken.getToken())
					.setRefreshToken(accessToken.getRefreshToken())
					.setAuthentication(accessToken.getAuthentication()).setExpiredDateAndTime(accessToken.getExpiredDateAndTime()));
		}
		return true;
	}

}

