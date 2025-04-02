package com.example.emailauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(content);
    }
} 