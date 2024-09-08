package com.SSPL.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdfDataService adfDataService;

    @Override
    public void run(String... args) throws Exception {
        // Simulate or fetch data to insert


        System.out.println("Initial data inserted into the database.");
    }
}
