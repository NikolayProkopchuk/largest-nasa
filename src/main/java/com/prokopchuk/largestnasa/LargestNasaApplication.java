package com.prokopchuk.largestnasa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LargestNasaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LargestNasaApplication.class, args);
    }

}
