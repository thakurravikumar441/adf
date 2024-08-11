package com.SSPL.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class ArduinoController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/")
    public String index(Model model) {
        return "Home";
    }
    @GetMapping("/Config")
    public String config() {
        return "Config";
    }
    @GetMapping("/test")
    public String Test() {
        return "testapge";
    }

    @RequestMapping("/arduino-data")
    public SseEmitter streamArduinoData() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void sendDataToClients(String data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(data);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
