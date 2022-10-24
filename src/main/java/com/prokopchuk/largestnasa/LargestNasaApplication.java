package com.prokopchuk.largestnasa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
@EnableCaching
public class LargestNasaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LargestNasaApplication.class, args);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
          .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
          .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
          .build();
    }

}
