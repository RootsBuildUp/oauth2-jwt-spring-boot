package com.RootBuildUp.oauth2jwtspringboot.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class CustomAuthenticationException {
    public static void handle(HttpServletResponse response, String message) {
        try {
            response.setStatus(252);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            ApiError apiError = new ApiError(252, "AuthenticationError", message);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, apiError);
        } catch (Exception e) {
            //System.out.println(e);
        }

    }
}
