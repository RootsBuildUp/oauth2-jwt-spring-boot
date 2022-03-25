package com.RootBuildUp.oauth2jwtspringboot.security;

import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Value("${check-user-scopes}")
    private Boolean checkUserScopes;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    /**
     * Request factory. step 4
     * @return
     */
    @Bean
    public OAuth2RequestFactory requestFactory() {
        System.out.println("------------request factory---------------");
        CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(clientDetailsService);
        requestFactory.setCheckUserScopes(true);
        return requestFactory;
    }


    /**
     * Token store in database. step 5
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        System.out.println("----------------token store----------------");
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JWT access token converter. step 6
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        System.out.println("------------jwtAccessTokenConverter----------------");
        JwtAccessTokenConverter converter = new CustomTokenEnhancer();
        converter.setSigningKey(VariableName.RSE_PRIVATE_KEY);
        converter.setVerifierKey(VariableName.RSE_PUBLIC_KEY);
        return converter;
    }

    /**
     * Token end point authenticator. step 3
     * @return
     */
    @Bean
    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter() {
        System.out.println("----------------tokenEndpointAuthenticationFilter-----------------");
        return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory());
    }

    /**
     * Check token scope. step 7
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        System.out.println("------------------AuthorizationServerEndpointsConfigurer--------------");
        endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        if (checkUserScopes)
            endpoints.requestFactory(requestFactory());
    }

    /**
     * Configurer method to communicate with DB(Authorization Server). step 8
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        System.out.println("-----------ClientDetailsServiceConfigurer------------------");
        clients
                .inMemory().withClient(VariableName.CLIENT_ID)
                .secret(passwordEncoder.encode(VariableName.CLIENT_SECRET))
                .authorizedGrantTypes(VariableName.GRANT_TYPE_PASSWORD, VariableName.AUTHORIZATION_CODE, VariableName.REFRESH_TOKEN, VariableName.IMPLICIT )
                .scopes(VariableName.SCOPE_READ, VariableName.SCOPE_WRITE,VariableName.TRUST)
                .accessTokenValiditySeconds(VariableName.ACCESS_TOKEN_VALIDITY_SECONDS)
                .refreshTokenValiditySeconds(VariableName.REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    /**
     * Check token access. step 9
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        System.out.println("-----------------AuthorizationServerSecurityConfigurer--------------");
        oauthServer.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder);
    }


}
