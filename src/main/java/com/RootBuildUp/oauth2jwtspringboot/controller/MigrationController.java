package com.RootBuildUp.oauth2jwtspringboot.controller;

import com.RootBuildUp.oauth2jwtspringboot.model.Login;
import com.RootBuildUp.oauth2jwtspringboot.service.AccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MigrationController {
    private final AccessTokenService accessTokenService;

    @GetMapping("/dataMigration")
    public Object dataMigration(){
        return accessTokenService.tokenDataMoveDBToRedis();
    }
}
