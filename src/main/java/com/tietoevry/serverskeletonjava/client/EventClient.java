package com.tietoevry.serverskeletonjava.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tietoevry.serverskeletonjava.model.Event;
import com.tietoevry.serverskeletonjava.repository.EventRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String token;
    private final EventRepository eventRepository;

    public EventClient(RestTemplate restTemplate, String baseUrl, String token, EventRepository eventRepository) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.token = token;
        this.eventRepository = eventRepository;
    }

        //Method for fetching events starting from a specific sequence number, eg all events from 13 and up 
        //https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/UriComponentsBuilder.html

        public String fetchEventsFromSequence(int sequence) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .pathSegment("events", "from", String.valueOf(sequence))
        .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        return response.getBody();
    }

    //Method for fetching a specific event by sequence number
    public String fetchEventBySequence(int sequence) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("events", String.valueOf(sequence))
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        return response.getBody();
    }

 
    //Method for fetching every event 
    public void fetchAndSaveEvents() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .pathSegment("events")
        .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        
        //Saving to database 
        saveEventsToDatabase(response.getBody());
    }


    //Method for saving the events to the database 
    private void saveEventsToDatabase(String responseBody) {
        //ObjectMapper used for converting JSON strings into Java objects

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //Starting with splitting the response body into lines so i can find the "data: " line
            //StringBuilder used to accumulate the JSON data for each event.

            String[] lines = responseBody.split("\n");
            StringBuilder dataBuilder = new StringBuilder();

            for (String line : lines) {
                // Check if the line starts with "data: "
                if (line.startsWith("data: ")) {
                    dataBuilder.append(line.substring(6));  // Append JSON part
                } else if (line.isEmpty()) {  // End of one event
                    if (dataBuilder.length() > 0) {
                        processEvent(objectMapper, dataBuilder.toString());
                        dataBuilder.setLength(0);  // Clear the builder for the next event
                    }
                }
            }

            // This handles cases where the last event doesn't end with a newline.
            if (dataBuilder.length() > 0) {
                processEvent(objectMapper, dataBuilder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //https://www.baeldung.com/jackson-object-mapper-tutorial --> 3.3 JSON to Jackson JsonNode
    // Process individual event and save to the database
    private void processEvent(ObjectMapper objectMapper, String eventData) throws IOException {
        // Parsing the JSON string into a JsonNode object
        JsonNode node = objectMapper.readTree(eventData);
        
        // Create a new Event object and set its fields from the JsonNode
        Event event = new Event();
        event.setSocSecNum(node.get("socSecNum").asText());
        event.setEventType(node.get("eventType").asText());
        
        //value is the Person information, this should be stored in another table but I ran out of time. 
        event.setValue(node.get("value").toString());
        
        // Parsing the timestamp from the JSON and set it in the Event object
        String timestampStr = node.get("timestamp").asText();
        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);
        event.setTimestamp(timestamp);
        
        //Saving to database
        eventRepository.save(event);
    }
}

/*Out of time to get task 5 to actually work, but i would do something along the lines of: 

private void updatePerson(JsonNode eventNode){

    get the social security number from the event
    String socSecNum = eventNode.get("socSecNum").asText();

    Find the person in the repository by social security number, or create a new person if not found
    Person person = personRepository.findById(socSecNum).orElse(new Person());

    Determine the type of event and update the person's information:
    
    switch (eventNode.get("eventType").asText()) {
        case "PERSON_CREATED":
            // If the event is PERSON_CREATED, update all the person's details
            JsonNode value = eventNode.get("value");
            person.setName(value.get("name").asText());
            person.setAddress(value.get("address").asText());
            person.setEmail(value.get("email").asText());
            person.setPhone(value.get("phone").asText());
            break;
        case "NAME_CHANGE":
            // If the event is NAME_CHANGE, update the person's name
            person.setName(eventNode.get("value").asText());
            break;
        case "ADDRESS_CHANGE":
            // If the event is ADDRESS_CHANGE, update the person's address
            person.setAddress(eventNode.get("value").asText());
            break;
        case "EMAIL_CHANGE":
            // If the event is EMAIL_CHANGE, update the person's email
            person.setEmail(eventNode.get("value").asText());
            break;
        case "PHONE_CHANGE":
            // If the event is PHONE_CHANGE, update the person's phone number
            person.setPhone(eventNode.get("value").asText());
            break;
        default:
            log warning or an error
}

*/