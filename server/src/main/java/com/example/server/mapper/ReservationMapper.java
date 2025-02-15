package com.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.server.entity.Reservation;
import java.util.List;

@Mapper
public interface ReservationMapper {
    void insert(Reservation reservation);
    List<Reservation> findByUserId(Long userId);
} 