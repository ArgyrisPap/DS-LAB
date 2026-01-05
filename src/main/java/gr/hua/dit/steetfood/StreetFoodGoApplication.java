package gr.hua.dit.steetfood;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreetFoodGoApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        // Μετατροπή των μεταβλητών σε System Properties για να τις βλέπει το Spring
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(StreetFoodGoApplication.class, args);
	}

}
