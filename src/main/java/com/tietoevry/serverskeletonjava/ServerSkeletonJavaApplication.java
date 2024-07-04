package com.tietoevry.serverskeletonjava;

import com.tietoevry.serverskeletonjava.client.TokenClient;
import com.tietoevry.serverskeletonjava.client.EventClient;
import com.tietoevry.serverskeletonjava.repository.EventRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServerSkeletonJavaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ServerSkeletonJavaApplication.class, args);

        Article article = new Article("Donald", "Fantonald");

        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        TokenClient tokenClient = new TokenClient(restTemplate, "http://localhost:8080/token");
        String token = tokenClient.getToken(article);
        System.out.println("Token: " + token);

        EventRepository eventRepository = context.getBean(EventRepository.class);
        EventClient eventClient = new EventClient(restTemplate, "http://localhost:8080", token, eventRepository);

        
        //System.out.println("Events from sequence 13: " + eventClient.fetchEventsFromSequence(13));
        System.out.println("Event with sequence 23: " + eventClient.fetchEventBySequence(23));

        eventClient.fetchAndSaveEvents();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
