package com.RootBuildUp.oauth2jwtspringboot.security;

import com.RootBuildUp.oauth2jwtspringboot.config.CustomAccessDeniedHandler;
import com.RootBuildUp.oauth2jwtspringboot.config.MyBasicAuthenticationEntryPoint;
import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private CustomTokenStore enhancer;
    /**
     * HttpSecurity configuration. step 10
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        System.out.println("10.---------------ResourceServerConfig HttpSecurity ------------------");
        http.cors().disable().
                authorizeRequests().antMatchers("/user", "/login").permitAll()
                .and()
                .authorizeRequests().antMatchers("/actuator**").permitAll()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/refreshToken").permitAll()
                .anyRequest().authenticated();
    }

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
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public BasicAuthenticationEntryPoint authenticationEntryPoint(){
        return new MyBasicAuthenticationEntryPoint();
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        System.out.println("6.------------resources Config----------------");

        resources.tokenStore(enhancer).accessDeniedHandler(accessDeniedHandler()).resourceId("oauth2-client");
    }

    }
