## JWT OAuth2 with Spring Boot

1. ### What is Spring Security?
       Spring Security is a framework focused on providingauthentication and authorization to Spring-based applications.
2. ### What is OAuth2?
       OAuth2 is an authorization framework to enable a third-party application to obtain limited access to an HTTP service through
       the sharing of an access OAuthToken. Its specification supersedes and obsoletes OAuth 1.0 protocol
2. ### What is JWT?
       JWT stands for JSON Web Token, a specification for the representation of claims to be transferred between two parties.
       The claims are encoded as a JSON object used as the payload of an encrypted structure which enablesthe claims to be digitally signed or encrypted.

   ![oauth2_with_jwt](images/oauth2-abstract-flow-diagram.jpg)
   

### The abstract OAuth 2.0 flow illustrated in Figure 1 describes the interaction between the four roles and includes the following steps:
    
    (1)  The client requests authorization from the resource owner.  The authorization request can be made directly to the resource owner (as shown), or preferably indirectly via
    the authorization server as an intermediary.
    
    (2)  The client receives an authorization grant, which is a credential representing the resource owner's authorization,expressed using one of four grant types defined in this
    specification or using an extension grant type.  The authorization grant type depends on the method used by the client to request authorization and the types supported by the
    authorization server.
    
    (3)  The client requests an access OAuthToken by authenticating with the authorization server and presenting the authorization grant.
    
    (4)  The authorization server authenticates the client and validates the authorization grant, and if valid, issues an access OAuthToken.
    
    (5)  The client requests the protected resource from the resource server and authenticates by presenting the access OAuthToken.
    
    (6)  The resource server validates the access OAuthToken, and if valid, serves the request.

###  OAuth2 Server (authorization server/resource owner and resource server)
    The app acts both as OAuth2 authorization server/resource owner and as resource server.
    The protected resources (as resource server) are published under /api/ path, while authentication path
    (as resource owner/authorization server) is mapped to /oauth/OAuthToken, following proposed default.

### Setup for Resource Owner and Authorization Server
    Authorization server behavior is enabled by the presence of @EnableAuthorizationServer annotation. Its configuration is merged with the one related to the resource owner behavior and both are contained in the class AuthorizationServerConfigurerAdapter.

#### The configurations applied here are related to:

- Client access (using ClientDetailsServiceConfigurer)
     - Selection of use an ***in-memory or JDBC*** based storage for client details with inMemory or jdbc methods
     - Client’s basic authentication using clientId and clientSecret (encoded with the chosen PasswordEncoder bean) attributes
     - Validity time for access and refresh tokens using accessTokenValiditySeconds and refreshTokenValiditySeconds attributes
     - Grant types allowed using authorizedGrantTypes attribute
     - Defines access scopes with scopes method
     - Identify client’s accessible resources
- Authorization server endpoint (using AuthorizationServerEndpointsConfigurer)
     - Define the use of a JWT OAuthToken with accessTokenConverter
     - Define the use of an UserDetailsService and AuthenticationManager interfaces to perform authentication (as resource owner)

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
        import org.springframework.security.oauth2.provider.OAuthToken.TokenStore;
        import org.springframework.security.oauth2.provider.OAuthToken.store.JwtAccessTokenConverter;
        import org.springframework.security.oauth2.provider.OAuthToken.store.JwtTokenStore;
        
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
         * Request factory.
         * @return
         */
        @Bean
        public OAuth2RequestFactory requestFactory() {
            CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(clientDetailsService);
            requestFactory.setCheckUserScopes(true);
            return requestFactory;
        }
    
    
        /**
         * Token store in database.
         * @return
         */
        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }
    
        /**
         * JWT access OAuthToken converter.
         * @return
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new CustomTokenEnhancer();
            converter.setSigningKey(VariableName.RSE_PRIVATE_KEY);
            converter.setVerifierKey(VariableName.RSE_PUBLIC_KEY);
            return converter;
        }
    
        /**
         * Configurer method to communicate with DB.
         */
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory().withClient(VariableName.CLIENT_ID)
                    .secret(passwordEncoder.encode(VariableName.CLIENT_SECRET))
                    .authorizedGrantTypes(VariableName.GRANT_TYPE_PASSWORD, VariableName.AUTHORIZATION_CODE, VariableName.REFRESH_TOKEN, VariableName.IMPLICIT )
                    .scopes(VariableName.SCOPE_READ, VariableName.SCOPE_WRITE,VariableName.TRUST)
                    .accessTokenValiditySeconds(VariableName.ACCESS_TOKEN_VALIDITY_SECONDS)
                    .refreshTokenValiditySeconds(VariableName.REFRESH_TOKEN_VALIDITY_SECONDS);
        }
    
        /**
         * Token end point authenticator.
         * @return
         */
        @Bean
        public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter() {
            return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory());
        }
    
    
        /**
         * Check OAuthToken access.
         */
        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.allowFormAuthenticationForClients()
                    .tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
                    .passwordEncoder(passwordEncoder);
        }
    
        /**
         * Check OAuthToken scope.
         */
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtAccessTokenConverter())
                    .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
            if (checkUserScopes)
                endpoints.requestFactory(requestFactory());
        }
  
      }


#### Document working............

### Document Reference

- **Document Link :** [Spring Security, OAuth2, JWT](https://www.toptal.com/spring/spring-boot-oauth2-jwt-rest-protection)
  **And** [oauth2-abstract-flow-diagram](https://www.rfc-editor.org/rfc/rfc6749)
