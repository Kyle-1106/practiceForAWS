package com.example.server.mapper;

import com.example.server.dto.request.RegisterRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    
    @Select("SELECT EXISTS (SELECT 1 FROM users WHERE email = #{email})")
    boolean existsByEmail(String email);
    
    @Insert("INSERT INTO users (name, email, password) VALUES (#{name}, #{email}, #{password})")
    void insert(RegisterRequest request);
} 