package com.example.server.Service;

import com.example.server.dto.request.RegisterRequest;
import com.example.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void register(RegisterRequest request) throws Exception {
        // メールアドレスの重複チェック
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new Exception("このメールアドレスは既に登録されています");
        }
        
        // ユーザー登録
        userMapper.insert(request);
    }
} 