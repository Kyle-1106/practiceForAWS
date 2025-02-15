package com.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.server.entity.User;

@Mapper
public interface LoginMapper {
    User findByEmail(String email);
    void saveRefreshToken(Long userId, String refreshToken);
    User findByRefreshToken(String refreshToken);
    void removeRefreshToken(Long userId);
} 