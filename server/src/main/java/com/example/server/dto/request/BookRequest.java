package com.example.server.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String publicationYear;
    private String coverImage;
    private LocalDate publishedDate;
    private String description;
} 