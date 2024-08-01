package com.SSPL.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class ArduinoService {

    @Autowired
    private ArduinoController arduinoController;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    arduinoController.sendDataToClients(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
