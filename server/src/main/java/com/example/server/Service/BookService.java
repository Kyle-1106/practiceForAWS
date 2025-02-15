package com.example.server.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.server.mapper.BookMapper;
import com.example.server.dto.request.BookRequest;
import com.example.server.entity.Book;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public List<Book> searchBooks(String title, String author, String isbn) {
        return bookMapper.searchBooks(title, author, isbn);
    }

    public List<Book> getBorrowedBooks(Long userId) {
        return bookMapper.findBorrowedBooks(userId);
    }

    public void registerBook(BookRequest request) throws Exception {
        // ISBNの重複チェック
        if (bookMapper.existsByIsbn(request.getIsbn())) {
            throw new Exception("この ISBN は既に登録されています");
        }
        System.out.println("request: " + request);

        // 書籍を登録
        bookMapper.insert(request);
    }

    public List<Book> getReservedBooks(Long userId) {
        try {
            return bookMapper.findReservedBooks(userId);
        } catch (Exception e) {
            System.out.println("予約本の取得エラー: " + e.getMessage());
            return new ArrayList<>(); // エラー時は空のリストを返す
        }
    }

} 