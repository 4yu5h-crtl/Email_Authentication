package com.example.emailauth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class QRCodeService {

    private static final int QR_CODE_SIZE = 200;
    private static final String QR_CODE_FORMAT = "PNG";
    private static final String QR_CODE_PREFIX = "data:image/png;base64,";
    private static final int CODE_LENGTH = 6;

    /**
     * Generate a random 6-digit numerical code for QR code verification
     */
    public String generateSecret() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        // Generate a 6-digit numerical code
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10)); // Generate a random digit (0-9)
        }
        
        return code.toString();
    }

    /**
     * Generate a QR code image as a Base64 data URL
     */
    public String generateQRCodeImage(String secret) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(secret, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, QR_CODE_FORMAT, outputStream);
            
            byte[] qrCodeBytes = outputStream.toByteArray();
            String base64QRCode = Base64.getEncoder().encodeToString(qrCodeBytes);
            
            return QR_CODE_PREFIX + base64QRCode;
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    /**
     * Verify a QR code secret
     */
    public boolean verifyQRCode(String secret, String userSecret) {
        return secret != null && secret.equals(userSecret);
    }
} 