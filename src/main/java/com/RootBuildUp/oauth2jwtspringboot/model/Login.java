package com.RootBuildUp.oauth2jwtspringboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class Login {
    private String username;
    private String password;
    private String refreshToken;
}
