package gr.hua.dit.steetfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
//FOR EMAILSERVICE
@EnableAsync
@Configuration
public class RestApiClientConfig {

    // TODO Get me from application properties!
    public static final String BASE_URL = "http://localhost:8081";  //for localhost

    //public static final String BASE_URL = "https://noc-service.onrender.com"; //for deploy

    public static final String GEOCODING_API_KEY = "3b7bf475ecad46dda2dc465bb402dc1f";

    public static final String GEOCODING_STATIC_MAP_BASE_URL = "https://maps.geoapify.com/v1/staticmap?";




    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
