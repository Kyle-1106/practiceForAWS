package com.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.server.entity.Book;
import com.example.server.dto.request.BookRequest;
import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> searchBooks(String title, String author, String isbn);
    Book findById(Long id);
    void updateStatus(Long id, String status);
    List<Book> findBorrowedBooks(Long userId);
    void insert(BookRequest book);
    boolean existsByIsbn(String isbn);
    Book findByIsbn(String isbn);
    List<Book> findReservedBooks(Long userId);
} 