package com.example.emailauth.service;

import com.example.emailauth.model.User;
import com.example.emailauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private QRCodeService qrCodeService;

    @Transactional
    public User registerUser(String name, Integer age, String email, String password) {
        // Check if email exists with a more robust check
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists. Please use a different email or try logging in.");
        }

        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        user.setQrVerified(false);

        // Generate and send OTP
        String otp = generateOTP();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        
        // Save user first to ensure it's in the database
        user = userRepository.save(user);
        
        // Then send email
        try {
            emailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            // If email sending fails, delete the user and throw exception
            userRepository.delete(user);
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
        
        return user;
    }

    public boolean verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        if (user.getOtp().equals(otp)) {
            user.setEnabled(true);
            user.setOtp(null);
            user.setOtpExpiryTime(null);
            
            // Generate QR code secret (6-digit code)
            String qrSecret = qrCodeService.generateSecret();
            user.setQrSecret(qrSecret);
            
            userRepository.save(user);
            return true;
        }

        return false;
    }
    
    public boolean resendOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        // Don't resend if user is already verified
        if (user.isEnabled()) {
            return false;
        }
        
        // Generate a new OTP
        String otp = generateOTP();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        
        // Save the updated OTP
        userRepository.save(user);
        
        // Send the new OTP via email
        try {
            emailService.sendOtpEmail(email, otp);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email. Please try again later.");
        }
    }
    
    public String generateQRCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (!user.isEnabled()) {
            throw new RuntimeException("User not verified");
        }
        
        if (user.isQrVerified()) {
            throw new RuntimeException("QR code already verified");
        }
        
        if (user.getQrSecret() == null || user.getQrSecret().isEmpty()) {
            // Generate a new QR secret (6-digit code) if it doesn't exist
            String qrSecret = qrCodeService.generateSecret();
            user.setQrSecret(qrSecret);
            userRepository.save(user);
        }
        
        return qrCodeService.generateQRCodeImage(user.getQrSecret());
    }
    
    public boolean verifyQRCode(String email, String secret) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (!user.isEnabled()) {
            throw new RuntimeException("User not verified");
        }
        
        if (user.isQrVerified()) {
            throw new RuntimeException("QR code already verified");
        }
        
        // Validate that the secret is a 6-digit code
        if (secret == null || !secret.matches("\\d{6}")) {
            throw new RuntimeException("Invalid code format. Please enter a 6-digit code.");
        }
        
        boolean verified = qrCodeService.verifyQRCode(secret, user.getQrSecret());
        
        if (verified) {
            user.setQrVerified(true);
            userRepository.save(user);
        }
        
        return verified;
    }
    
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .filter(User::isEnabled)
                .filter(User::isQrVerified);
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
} 