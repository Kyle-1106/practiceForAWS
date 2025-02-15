package com.example.server.Controller;

import com.example.server.dto.request.RegisterRequest;
import com.example.server.dto.response.RegisterResponse;
import com.example.server.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(RegisterResponse.builder()
                    .message("登録が完了しました")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(e.getMessage())
                    .success(false)
                    .build());
        }
    }
} 