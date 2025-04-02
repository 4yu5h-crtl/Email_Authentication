package com.example.emailauth.controller;

import com.example.emailauth.model.User;
import com.example.emailauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            Integer age = Integer.parseInt(request.get("age"));
            String email = request.get("email");
            String password = request.get("password");

            User user = userService.registerUser(name, age, email, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful. Please check your email for OTP.");
            response.put("email", email);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");

            boolean verified = userService.verifyOTP(email, otp);
            
            if (verified) {
                String qrCode = userService.generateQRCode(email);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "OTP verified successfully. Please scan the QR code.");
                response.put("qrCode", qrCode);
                response.put("email", email);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            boolean sent = userService.resendOTP(email);
            
            if (sent) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "OTP has been resent to your email.");
                response.put("email", email);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Failed to resend OTP. User not found or already verified.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/qr-code/{email}")
    public ResponseEntity<?> getQRCode(@PathVariable String email) {
        try {
            String qrCode = userService.generateQRCode(email);
            Map<String, Object> response = new HashMap<>();
            response.put("qrCode", qrCode);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-qr")
    public ResponseEntity<?> verifyQRCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String secret = request.get("secret");

            boolean verified = userService.verifyQRCode(email, secret);
            
            if (verified) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "QR code verified successfully. You can now login.");
                response.put("email", email);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid 6-digit code");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            Optional<User> user = userService.login(email, password);
            
            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("email", email);
                response.put("name", user.get().getName());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid credentials or user not verified");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 