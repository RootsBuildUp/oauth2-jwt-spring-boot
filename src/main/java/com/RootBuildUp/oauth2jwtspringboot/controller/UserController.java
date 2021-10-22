package com.RootBuildUp.oauth2jwtspringboot.controller;

import com.RootBuildUp.oauth2jwtspringboot.model.Login;
import com.RootBuildUp.oauth2jwtspringboot.model.User;
import com.RootBuildUp.oauth2jwtspringboot.service.TokenService;
import com.RootBuildUp.oauth2jwtspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    @Autowired private TokenService tokenService;
    @Autowired private UserService userService;
    @Autowired private PasswordEncoder encoder;

    @PostMapping("login")
    public Object getToken(@RequestBody Login login){
        return tokenService.tokenGenerate(login);
    }

    @GetMapping("refreshToken")
    public Object getTokenByRefreshToken(HttpServletRequest rq){
        return tokenService.tokenGenerateByRefreshToken(rq);
    }

    @GetMapping("user")
    public Object addUser(){
        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder.encode("password"));
        user.setRoles(Arrays.asList("ROLE_ADMIN","ROLE_USER"));
        return userService.create(user);
    }


}