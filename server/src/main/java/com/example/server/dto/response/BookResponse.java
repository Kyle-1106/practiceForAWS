package com.example.server.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private LocalDate publishedDate;
    private String description;
    private String coverImageUrl;
    private String status;
} 