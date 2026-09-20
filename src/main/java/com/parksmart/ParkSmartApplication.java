package com.parksmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkSmartApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  ParkSmart Management System is running!");
        System.out.println("  Access Dashboard: http://localhost:8080");
        System.out.println("=================================================");
    }
}
