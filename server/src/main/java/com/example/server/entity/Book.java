package com.example.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publicationYear;
    private String coverImage;
    private String status;  // 貸出可能、貸出中など
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;  // 返却期限を追加
} 