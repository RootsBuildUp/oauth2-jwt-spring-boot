package com.RootBuildUp.oauth2jwtspringboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * HttpSecurity configuration. step 10
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        System.out.println("---------------ResourceServerConfig HttpSecurity ------------------");
        http.cors().disable().
                authorizeRequests().antMatchers("/user", "/login","/refreshToken").permitAll()
                .and()
                .authorizeRequests().antMatchers("/actuator**").permitAll()
                .anyRequest().authenticated();
    }

    }
