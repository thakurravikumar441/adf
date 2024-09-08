package com.SSPL.demo;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

@RestController
public class ArduinoConnection {

    @Autowired
    private AdfDataService adfDataService;

    private SerialPort serialPort;

    private double azmat;
    private double elevation;
    private double range;
    private double latitude;
    private double longitude;


    private final double serverLatitude = 28.640597286241622;
    private final double serverLongitude = 77.04705327701325;

    @PostMapping("/api/serial-data")
    public ResponseEntity<String> receiveSerialData(@RequestBody PortConfig dataRequest) {
        String receivedData = dataRequest.getData();
        System.out.println("Data received from frontend: " + receivedData);

        // Parse the received data
        parseData(receivedData);

        // Save data to the database
        database adfData = new database();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Ensure format matches database column
        String currentTime = sdf.format(new Date());
        adfData.setTimestamp(currentTime);
        adfData.setAzmat(azmat);
        adfData.setElevation(elevation);
        adfData.setExpectedRange(range);
        // Save to database
        adfDataService.saveData(adfData);

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
                    azmat = Double.parseDouble(part.substring(2).trim());
                } else if (part.startsWith("ev")) {
                    elevation = Double.parseDouble(part.substring(2).trim());
                } else if (part.startsWith("r")) {
                    range = Double.parseDouble(part.substring(1).trim());
                }
            }

            // Convert azimuth angle from degrees to radians
            double azmatRad = toRadians(azmat);

            // Fixed latitude and longitude of the server
            double ServerLatitude = 28.640627304904214;
            double ServerLongitude = 77.04703758721507;

            // Earth radius in meters
            double earthRadius = 6371000;

            // Calculate the offsets
            double x_offset = range * cos(azmatRad);
            double y_offset = range * sin(azmatRad);

            // Calculate new latitude and longitude
            latitude = ServerLatitude + (y_offset / earthRadius) * (180 / Math.PI);
            longitude = ServerLongitude + (x_offset / earthRadius) * (180 / Math.PI) / cos(toRadians(ServerLatitude));

            System.out.println("Calculated Coordinates: Latitude " + latitude + "," + longitude);
            System.out.println("Azmat: " + azmat);
            System.out.println("Elevation: " + elevation);
            System.out.println("Range: " + range);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing data: " + e.getMessage());
        }
    }

    @GetMapping("/api/update-data")
    public ResponseEntity<Map<String, Double>> sendDataToFrontend() {
        Map<String, Double> response = new HashMap<>();
        response.put("azmat", azmat);
        response.put("elevation", elevation);
        response.put("range", range);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/map-geo")
    public ResponseEntity<Map<String, Double>> sendDataToMap() {
        Map<String, Double> response = new HashMap<>();
        response.put("serverLatitude", serverLatitude);
        response.put("serverLongitude", serverLongitude);
        response.put("latitude", latitude);
        response.put("longitude", longitude);
        return ResponseEntity.ok(response);
    }

    // Additional methods for serial port configuration and handling can be added here.
}
