package com.Login.page.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Şifreyi hashleyerek geri döndürür
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Girilen şifre ile hash’in eşleşip eşleşmediğini kontrol eder
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}