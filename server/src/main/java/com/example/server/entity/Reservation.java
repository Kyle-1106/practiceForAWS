package com.example.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private Long userId;
    private Long bookId;
    private String status;  // "予約中", "貸出中", "返却済み"
    private LocalDateTime reservedAt;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
} 