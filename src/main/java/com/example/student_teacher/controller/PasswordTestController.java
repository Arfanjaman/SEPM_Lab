package com.example.student_teacher.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordTestController {
    
    @GetMapping("/test/encode")
    public String encodePassword(@RequestParam String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
    
    @GetMapping("/test/match")
    public String testMatch(@RequestParam String plain, @RequestParam String encoded) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(plain, encoded);
        return "Matches: " + matches;
    }
}
