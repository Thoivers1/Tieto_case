package com.tietoevry.serverskeletonjava.client;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.tietoevry.serverskeletonjava.Article;

public class TokenClient {

    private final RestTemplate restTemplate;
    private final String url;

    public TokenClient(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    //Method for getting the token 
    //https://www.baeldung.com/spring-resttemplate-post-json

    public String getToken(Article article) {
        
        //Set headers for the request

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Creating a map in order to send username and password in the request body 
        //https://docs.spring.io/spring-framework/reference/web/webflux-webclient/client-body.html

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", article.getUsername());
        map.add("password", article.getPassword());

        //HttpEntity with the headers and request body information
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        //Sending a Post request to the url 
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        //Returning the token 
        return extractTokenFromResponse(response.getBody());
    }

    //Helper method in order to extract the token from the JSON resonse 
    private static String extractTokenFromResponse(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getString("token");
    }
}
