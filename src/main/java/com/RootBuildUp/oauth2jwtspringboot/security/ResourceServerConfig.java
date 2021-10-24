package com.RootBuildUp.oauth2jwtspringboot.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * HttpSecurity configuration.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.cors().disable().
                authorizeRequests().antMatchers("/user", "/login","/refreshToken").permitAll()
                .and()
                .authorizeRequests().antMatchers("/actuator**").permitAll()
                .anyRequest().authenticated();
    }

    }
