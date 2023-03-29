package product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.web.reactive.function.client.WebClient;
@SpringBootApplication
public class Application {
    public static void main(String args[])
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }
}