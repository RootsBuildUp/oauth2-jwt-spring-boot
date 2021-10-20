package com.RootBuildUp.oauth2jwtspringboot.service;

import com.RootBuildUp.oauth2jwtspringboot.model.Login;
import com.RootBuildUp.oauth2jwtspringboot.util.HRApi;
import com.RootBuildUp.oauth2jwtspringboot.util.Util;
import com.RootBuildUp.oauth2jwtspringboot.util.VariableName;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Component
public class TokenService {

    private static Util util;


    public Object tokenGenerate(Login login){
        util = Util.getInstance();
        // HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        //
        // Authentication
        //
        String auth = VariableName.CLIENT_ID + ":" + VariableName.CLIENT_SECRET;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = VariableName.BASIC_AUTH + new String(encodedAuth);
        headers.set(VariableName.AUTHENTICATION, authHeader);
        // Request to return JSON format
        //    headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(VariableName.CONTENT_TYPE,VariableName.FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(VariableName.USER_NAME, login.getUsername());
        map.add(VariableName.PASSWORD, login.getPassword());
        map.add(VariableName.GRANT_TYPE, VariableName.GRANT_TYPE_PASSWORD);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        System.out.println(request);
        // RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Send request with Post method, and Headers.
        ResponseEntity<Object> response = restTemplate.postForEntity(HRApi.USER_TOKEN,request, Object.class);

        Object result = response.getBody();

        return result;
    }
}
