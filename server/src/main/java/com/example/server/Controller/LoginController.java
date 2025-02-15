package com.example.server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.server.dto.request.LoginRequest;
import com.example.server.dto.response.LoginResponse;
import com.example.server.Service.LoginService;

@RestController
@RequestMapping("/api")
// @CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
          System.out.println(request.getEmail());
          System.out.println(request.getPassword());
            LoginResponse response = loginService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
          System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

