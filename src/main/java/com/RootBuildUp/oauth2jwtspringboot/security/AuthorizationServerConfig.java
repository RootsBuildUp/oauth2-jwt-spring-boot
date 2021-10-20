package com.RootBuildUp.oauth2jwtspringboot.security;

import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(VariableName.RSE_PRIVATE_KEY);
        converter.setVerifierKey(VariableName.RSE_PUBLIC_KEY);
        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(
                Arrays.asList(customTokenEnhancer, tokenEnhancer()));

        endpoints.authenticationManager(authenticationManager)
                .tokenEnhancer(chain)
                .tokenStore(tokenStore())
                .accessTokenConverter(tokenEnhancer());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(VariableName.CLIENT_ID).secret(VariableName.CLIENT_SECRET)
                .scopes(VariableName.SCOPE_READ, VariableName.SCOPE_WRITE)
                .authorizedGrantTypes(VariableName.GRANT_TYPE_PASSWORD, VariableName.REFRESH_TOKEN)
                .accessTokenValiditySeconds(VariableName.ACCESS_TOKEN_VALIDITY_SECONDS)
                .refreshTokenValiditySeconds(VariableName.REFRESH_TOKEN_VALIDITY_SECONDS);

    }
}
