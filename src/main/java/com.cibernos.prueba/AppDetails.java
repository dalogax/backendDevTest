package com.cibernos.prueba;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppDetails {
    public static void main(String[] args) {
        System.out.print("The addition of ");
        SpringApplication.run(AppDetails.class, args);

    }
}