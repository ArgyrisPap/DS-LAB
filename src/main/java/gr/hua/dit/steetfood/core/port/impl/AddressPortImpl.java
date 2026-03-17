package gr.hua.dit.steetfood.core.port.impl;

import gr.hua.dit.steetfood.config.RestApiClientConfig;
import gr.hua.dit.steetfood.core.port.AddressPort;
import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;

import gr.hua.dit.steetfood.core.service.impl.OrderServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AddressPortImpl implements AddressPort {
    private final RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressPortImpl.class);

    @Value("${geocoding.api.key}")
    private  String geocodingApiKey;

    @Value ("${geocoding.static-map.url}")
    private String staticMapUrl;

    public AddressPortImpl(RestTemplate restTemplate) {
        if (restTemplate == null) {throw new NullPointerException();}
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<AddressResult> findAdress(String rawAddress) {
        if (rawAddress == null || rawAddress.isBlank()) {
            return Optional.empty();
        }

        // Δημιουργία URI με σωστό encoding (UTF-8)
        URI uri = UriComponentsBuilder
            .fromHttpUrl("https://nominatim.openstreetmap.org/search")
            .queryParam("format", "json")
            .queryParam("q", rawAddress)
            .queryParam("countrycodes", "gr")
            .queryParam("limit", "1")
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUri();

        // Δημιουργία headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "StreetFoodGO-App/1.0 (contact: arg.papa97@gmail.com)");
        headers.set("Accept", "*/*");
        headers.set("Accept-Language", "en-US,en;q=0.9");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        System.out.println(uri.toString());

        try {
            // Πρώτα κάνουμε request ως String για debugging
            ResponseEntity<String> debugResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            System.out.println("Raw JSON response: " + debugResponse.getBody());

            // Μετά κάνουμε mapping στο DTO
            ResponseEntity<AddressResult[]> response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, AddressResult[].class);

            if (response.getBody() == null || response.getBody().length == 0) {
                LOGGER.warn("======ADDRESS NOT FOUND FOR!: {}", rawAddress);
                return Optional.empty();
            }

            AddressResult result = response.getBody()[0];

            // Προαιρετικά, parse lat/lon σε double για χρήση στο χάρτη
            try {
                Double.parseDouble(result.lat());
                Double.parseDouble(result.lon());
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid coordinates for address {}: {}", rawAddress, e.getMessage());
                return Optional.empty();
            }

            return Optional.of(result);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling Nominatim API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Unexpected error for address {}: {}", rawAddress, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String getStaticMap(Double latitude, Double longitude) {
        String style = "style=osm-bright";
        String size= "&width=400&height=200&center=lonlat:";
        String marker = "&marker=lonlat:"+longitude+","+latitude;

        String url =staticMapUrl+
            style+size+longitude+","+latitude+"&zoom=14"+marker+"&apiKey="+
            geocodingApiKey;
        System.out.println(url);  //Cordinates are correct, next update, we will return the img where the source is from openstreetmap
        return url;
    }
}
