package gr.hua.dit.steetfood.core.port.impl;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.hua.dit.steetfood.core.port.RoutePort;
import gr.hua.dit.steetfood.core.port.impl.dto.RouteInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoutePortImpl implements RoutePort {


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ors.api.key}")
    private String apiKey;

    public RoutePortImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        if (restTemplate == null) throw new NullPointerException();
        if (objectMapper == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public RouteInfo getRoute(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon2==null || lon1==null || lat2==null)throw new NullPointerException();

        String url = "https://api.openrouteservice.org/v2/directions/driving-car"
            + "?api_key=" + apiKey
            + "&start=" + lon1 + "," + lat1
            + "&end=" + lon2 + "," + lat2;

        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode summary = root
                .path("features").get(0)
                .path("properties")
                .path("summary");

            double duration = summary.path("duration").asDouble();
            double distance = summary.path("distance").asDouble();

            return new RouteInfo(duration, distance);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse route response", e);
        }

    }
}

