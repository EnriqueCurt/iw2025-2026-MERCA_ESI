package com.example.iw20252026merca_esi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Iw20252026MercaEsiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Iw20252026MercaEsiApplication.class, args);
    }

}
