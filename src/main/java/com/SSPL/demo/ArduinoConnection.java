package com.SSPL.demo;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArduinoConnection {

    private SerialPort serialPort;

    @PostMapping("/configure-port")
    public String configurePort(@RequestBody PortConfig portConfig) {
        int portNumber = portConfig.getPortNumber();
        int bitRate = portConfig.getBitRate();

        // Modify portName based on your actual port name format
        String portName = "COM" + portNumber; // Example: "COM3" or "/dev/ttyUSB0"

        try {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }

            SerialPort[] portList = SerialPort.getCommPorts();
            for (SerialPort port : portList) {
                if (port.getSystemPortName().equals(portName)) {
                    serialPort = port;
                    break;
                }
            }

            if (serialPort == null) {
                return "Port not found";
            }

            serialPort.setBaudRate(bitRate);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.openPort();

            return "Port configured and connected successfully";

        } catch (Exception e) {
            return "Error configuring port: " + e.getMessage();
        }
    }

    @GetMapping("/connection-status")
    public String connectionStatus() {
        if (serialPort != null && serialPort.isOpen()) {
            return "Port is open and connected";
        } else {
            return "Port is not connected";
        }
    }

    @PostMapping("/send-data")
    public String sendData(@RequestParam int data) {
        try {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.getOutputStream().write(data);
                return "Data sent successfully";
            } else {
                return "Port is not connected";
            }
        } catch (Exception e) {
            return "Error sending data: " + e.getMessage();
        }
    }

    @PostMapping("/disconnect")
    public String disconnect() {
        try {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
                return "Disconnected successfully";
            } else {
                return "Port is not connected";
            }
        } catch (Exception e) {
            return "Error disconnecting: " + e.getMessage();
        }
    }
}
