package com.RootBuildUp.oauth2jwtspringboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String,Object> obj = new HashMap<>();
        obj.put("error","invalid_token");
        obj.put("error_description",authException.getMessage());
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper= new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(obj);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        out.print(jsonString);
        out.flush();

    }
    public static final String REALM_NAME = "memorynotfound.com";
    @Override
    public void afterPropertiesSet() {
        setRealmName(REALM_NAME);
        super.afterPropertiesSet();
    }
}