package com.SSPL.demo;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArduinoConnection {

    private SerialPort serialPort;

    private int azmat;
    private int elevation;
    private int range;

    @PostMapping("/api/serial-data")
    public ResponseEntity<String> receiveSerialData(@RequestBody PortConfig dataRequest) {
        String receivedData = dataRequest.getData();
        System.out.println("Data received from frontend: " + receivedData);

        // Parse the received data
        parseData(receivedData);

        // Optionally, you could send this data to a connected serial port:
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.writeBytes(receivedData.getBytes(), receivedData.length());
        }

        return ResponseEntity.ok("Data received and processed");
    }

    private void parseData(String data) {
        try {
            // Remove any extra whitespace and split the string by spaces
            String[] parts = data.trim().split("\\s+");

            for (String part : parts) {
                // Trim any leading or trailing whitespace from each part
                part = part.trim();

                if (part.startsWith("AZ")) {
                    azmat = Integer.parseInt(part.substring(2).trim());
                } else if (part.startsWith("ev")) {
                    elevation = Integer.parseInt(part.substring(2).trim());
                } else if (part.startsWith("r")) {
                    range = Integer.parseInt(part.substring(1).trim());
                }
            }

            // Print values for debugging
            System.out.println("Azmat: " + azmat);
            System.out.println("Elevation: " + elevation);
            System.out.println("Range: " + range);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing data: " + e.getMessage());
        }
    }


    // Additional methods for serial port configuration and handling can be added here.
}
