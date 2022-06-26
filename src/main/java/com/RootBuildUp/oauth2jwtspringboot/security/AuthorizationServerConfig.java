package com.RootBuildUp.oauth2jwtspringboot.security;

import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Value("${check-user-scopes}")
    private Boolean checkUserScopes;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Autowired
    private CustomTokenStore enhancer;

    @Autowired
    private Environment env;

//    /**
//     * Token end point authenticator. step 3
//     * @return
//     */
//    @Bean
//    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter() {
//        System.out.println("3.----------------tokenEndpointAuthenticationFilter-----------------");
//        return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory());
//    }

//    /**
//     * Request factory. step 4
//     * @return
//     */
//    @Bean
//    public OAuth2RequestFactory requestFactory() {
//        System.out.println("4.------------request factory---------------");
//        CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(clientDetailsService);
//        requestFactory.setCheckUserScopes(false);
//        return requestFactory;
//    }


    /**
     * Token store in database. step 5
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        System.out.println("5.----------------token store----------------");
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JWT access token converter. step 6
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        System.out.println("6.------------jwtAccessTokenConverter----------------");
        JwtAccessTokenConverter converter = new CustomTokenEnhancer();
        converter.setSigningKey(VariableName.RSE_PRIVATE_KEY);
        converter.setVerifierKey(VariableName.RSE_PUBLIC_KEY);
        return converter;
    }

    /**
     * Check token scope. step 7
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        System.out.println("7.------------------AuthorizationServerEndpointsConfigurer--------------");
        endpoints.tokenStore(tokenStore()).tokenStore(enhancer).tokenEnhancer(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
//        if (checkUserScopes)
//            endpoints.requestFactory(requestFactory());
    }

    /**
     * Configurer method to communicate with DB(Authorization Server). step 8
     *
     * Let's now define the SQL structure for storing our OAuth clients:
     *----------------------------Create Custom Oauth client table
     * create table oauth_client_details (
     *     client_id VARCHAR(256) PRIMARY KEY,
     *     resource_ids VARCHAR(256),
     *     client_secret VARCHAR(256),
     *     scope VARCHAR(256),
     *     authorized_grant_types VARCHAR(256),
     *     web_server_redirect_uri VARCHAR(256),
     *     authorities VARCHAR(256),
     *     access_token_validity INTEGER,
     *     refresh_token_validity INTEGER,
     *     additional_information VARCHAR(4096),
     *     autoapprove VARCHAR(256)
     * );
     * --------------------------------------------End---------------------------------------
     * ------------------------------------insert query for custom oauth client_details-----------
     * INSERT INTO public.oauth_client_details(
     * 	client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
     * 	VALUES
     * 		('oauth2-client', null, passwordEncoder.encode(VariableName.CLIENT_SECRET), 'all,read,write,trust',
     * 	'password','authorization_code','refresh_token', 36000, 36000, null, 'true');
     * 	------------------------------------END----------------------------------------------------
     * The most important fields from the oauth_client_details we should focus on are:
     *
     * client_id – to store the id of newly registered clients
     * client_secret – to store the password of clients
     * access_token_validity – which indicates if client is still valid
     * authorities – to indicate what roles are permitted with particular client
     * scope – allowed actions, for example writing statuses on Facebook etc.
     * authorized_grant_types, which provides information how users can login to the particular client (in our example case it's a form login with password)
     * Please note, that each client has one to many relationship with users, which naturally means that multiple users can utilize a single client.
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        System.out.println("8.-----------ClientDetailsServiceConfigurer------------------");
        clients.jdbc(dataSource());
        System.out.println(passwordEncoder.encode(VariableName.CLIENT_SECRET));
        /**
         *clients.inMemory().withClient(VariableName.CLIENT_ID)
         *                 .secret(passwordEncoder.encode(VariableName.CLIENT_SECRET))
         *                 .authorizedGrantTypes(VariableName.GRANT_TYPE_PASSWORD, VariableName.AUTHORIZATION_CODE, VariableName.REFRESH_TOKEN, VariableName.IMPLICIT )
         *                 .scopes(VariableName.SCOPE_READ, VariableName.SCOPE_WRITE,VariableName.TRUST)
         *                 .accessTokenValiditySeconds(VariableName.ACCESS_TOKEN_VALIDITY_SECONDS)
         *                 .refreshTokenValiditySeconds(VariableName.REFRESH_TOKEN_VALIDITY_SECONDS);
         */
    }

    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    /**
     * Check token access. step 9
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        System.out.println("9.-----------------AuthorizationServerSecurityConfigurer--------------");
        oauthServer.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder);
    }


}
