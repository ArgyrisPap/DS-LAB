package gr.hua.dit.steetfood.core.port.impl;

import gr.hua.dit.steetfood.config.RestApiClientConfig;
import gr.hua.dit.steetfood.core.port.AddressPort;
import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AddressPortImpl implements AddressPort {
    private final RestTemplate restTemplate;

    public AddressPortImpl(RestTemplate restTemplate) {
        if (restTemplate == null) {throw new NullPointerException();}
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<AddressResult> findAdress (String formattedAddress) {
        //if (rawAdress == null) {throw new NullPointerException();}
        //TODO NOT NULL ELEGXOI
        //HTTP Request
        //----------------------------
        //convert raw Address to right form (+ spaces + spaces)
        final String url = "https://nominatim.openstreetmap.org/search?format=json&q="+formattedAddress;
        //System.out.println(url);
        final ResponseEntity<AddressResult[]> response =
            restTemplate.getForEntity(url, AddressResult[].class);

        if (response.getBody() == null || response.getBody().length == 0) {
            System.out.println(url);
            return Optional.empty();
            //throw new RuntimeException("Address not found");
        }
        AddressResult result = response.getBody()[0]; //ONLY FIRST SEARCH RESULT
        //System.out.println("latitude="+result.lat()+" longitude="+result.lon()
        //+"displayName="+result.display_name());
        return  Optional.of(result);

    }

    @Override
    public String getStaticMap(Double latitude, Double longitude) {
        String style = "style=osm-bright";
        String size= "&width=400&height=200&center=lonlat:";

        String url =RestApiClientConfig.GEOCODING_STATIC_MAP_BASE_URL+
            style+size+longitude+","+latitude+"&zoom=14&apiKey="+
            RestApiClientConfig.GEOCODING_API_KEY;
        System.out.println(url);  //Cordinates are correct, next update, we will return the img where the source is from openstreetmap
        return url;
    }
}
