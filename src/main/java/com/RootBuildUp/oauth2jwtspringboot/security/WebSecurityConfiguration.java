package com.RootBuildUp.oauth2jwtspringboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Rashedul Islam
 * @since 20-10-2021
 */

/**
 * The @EnableWebSecurity annotation and WebSecurityConfigurerAdapter
 * work together to provide web based security.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    /**
     * Password encoder. step 1
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("1.------------------passwordEncoder------------");
        if(passwordEncoder == null) passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;
    }

    /**
     * Authentication manager bean. step 2
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        System.out.println("2.---------------authenticationManagerBean--------------");
        return super.authenticationManagerBean();
    }


    /**
     * Authenticator manager builder. step 11
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("11.----------------AuthenticationManagerBuilder-------------");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * CROSS configuration. step 12
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        System.out.println("12.-------------WebSecurity------------------");
        web.ignoring().antMatchers("/login","/refreshToken", "/user", "/actuator/**");
    }


}
