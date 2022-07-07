package com.RootBuildUp.oauth2jwtspringboot.security;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import com.RootBuildUp.oauth2jwtspringboot.redis.service.OAthTokenRepoImpl;
import com.RootBuildUp.oauth2jwtspringboot.service.AccessTokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@EnableAsync
@Component
@Slf4j
public class CustomTokenStore implements TokenStore {





	@Autowired
	private AccessTokenService accService;

	@Autowired
	private OAthTokenRepoImpl oAthTokenRepo;

	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();



	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		log.info("it came here readAuthentication");
		String username = null;
		String clientId = null;
		try {
			DecodedJWT jwt = com.auth0.jwt.JWT.decode(token.getValue());
			username = jwt.getClaims().get("user_name").asString();
			clientId = jwt.getClaims().get("client_id").asString();
		}catch (Exception e){
			return null;
		}


		if(username != null && clientId != null){
			com.service.cblmodel.redis.model.OAuthToken oAthToken = oAthTokenRepo.findById(username+clientId);
			if(token.getValue().equals(oAthToken.getToken())) {
				accService.asyncMethodForTokenTimeUpdate(username+clientId,oAthToken);
				return getOAuth2Authentication(token.getValue());
			}
			else return null;
		}
		return null;
	}

	private OAuth2Authentication getOAuth2Authentication(String token){
		DecodedJWT jwt = com.auth0.jwt.JWT.decode(token);
		String username = jwt.getClaims().get("user_name").asString();
		String clientId = jwt.getClaims().get("client_id").asString();
		HashMap<String, String> authorizationParameters = new HashMap<String, String>();

		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		Set<String> responseType = new HashSet<String>();

		Set<String> scopes = new HashSet<String>();
		OAuth2Request authorizationRequest = new OAuth2Request(
				authorizationParameters, clientId,
				authorities, true,scopes, null, "",
				responseType, null);

		User userPrincipal = new User(username, "", true, true, true, true, authorities);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				userPrincipal, null, authorities);

		OAuth2Authentication authenticationRequest = new OAuth2Authentication(
				authorizationRequest, authenticationToken);
		authenticationRequest.setAuthenticated(true);

		return authenticationRequest;
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {

		log.info("it came here readAuthentication2");
		return null;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		/*

		java.sql.Date expire =new java.sql.Date(token.getExpiration().getTime());    //convert normal date to sql date
		Jwt jwtToken = JwtHelper.decode(token.toString());    //Decode jwt token
	    String claims = jwtToken.getClaims();										//return jwt token in string format

	   */

	    log.info("it came here storeAccessToken");
		accService.asyncMethodForTokenStore(token,authentication);


	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		String username = null;
		String clientId = null;
		try {
			DecodedJWT jwt = com.auth0.jwt.JWT.decode(tokenValue);
			username = jwt.getClaims().get("user_name").asString();
			clientId = jwt.getClaims().get("client_id").asString();
		}catch (Exception e){

			return null;
		}

		if(username != null && clientId != null){
			log.info("it came here readAccessToken");
			com.service.cblmodel.redis.model.OAuthToken oAthToken = oAthTokenRepo.findById(username+clientId);
			if(oAthToken.getExpiredDateAndTime().isBefore(LocalDateTime.now())){
				throw new InvalidTokenException("Token Date Expired");
			}
			else if(tokenValue.equals(oAthToken.getToken())) return getOAuth2AccessToken(tokenValue);
			else return null;
		}
		return null;
	}

	private OAuth2AccessToken getOAuth2AccessToken(String tokenValue){
		return new OAuth2AccessToken() {
			@Override
			public Map<String, Object> getAdditionalInformation() {
				return null;
			}

			@Override
			public Set<String> getScope() {
				return null;
			}

			@Override
			public OAuth2RefreshToken getRefreshToken() {
				return null;
			}

			@Override
			public String getTokenType() {
				return null;
			}

			@Override
			public boolean isExpired() {
				return false;
			}

			@Override
			public Date getExpiration() {
				return null;
			}

			@Override
			public int getExpiresIn() {
				return 0;
			}

			@Override
			public String getValue() {
				return tokenValue;
			}
		};
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {

		log.info("it came here removeAccessToken");

	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

		log.info("it came here storeRefreshToken");

		/*
		String username=authentication.getName();
		System.out.println("store refresh user"+username);

		List<RefreshToken> tokens=refService.findByUserName(username);

	    if(tokens!= null)
	    {
	    	refService.deleteByUser(username);
	    }

		refService.create(new RefreshToken(UUID.randomUUID().toString()+UUID.randomUUID().toString(),extractTokenKey(refreshToken.getValue()), refreshToken.getValue(),authentication.getName(), authentication));
		*/
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {

		log.info("It came here readRefreshToken and token is {}",tokenValue);

		log.info("After {}",new DefaultOAuth2RefreshToken(tokenValue));

		return new DefaultOAuth2RefreshToken(tokenValue);
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {

		log.info("it came here readAuthenticationForRefreshToken with token {}", token.getValue());

//		AccessToken at = accService.findByRefreshToken(token.getValue());


		return getOAuth2Authentication(token.getValue());


	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		log.info("it came here removeRefreshToken");


	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		log.info("it came here removeAccessTokenUsingRefreshToken");

	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {

		log.info("it came here getAccessToken");

		return null;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {

		log.info("it came here findTokensByClientIdAndUserName");
		return null;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {

		log.info("it came here findTokensByClientId");
		return null;
	}


	private String extractTokenKey(String value) {
        if(value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] e = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new Object[]{new BigInteger(1, e)});
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }

}
