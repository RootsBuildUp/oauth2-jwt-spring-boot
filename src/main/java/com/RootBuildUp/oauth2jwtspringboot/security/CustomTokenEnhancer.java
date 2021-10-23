package com.RootBuildUp.oauth2jwtspringboot.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer extends JwtAccessTokenConverter {


    /**
     * Enhance access token. This will send as response.
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        System.out.println("enhance access Token");

        User user = (User) authentication.getPrincipal();
        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        info.put("username", user.getUsername());
        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(info);
        return super.enhance(customAccessToken, authentication);
    }
}
