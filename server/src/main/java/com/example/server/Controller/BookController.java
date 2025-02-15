package com.example.server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.server.Service.BookService;
import com.example.server.entity.Book;
import com.example.server.dto.request.BookRequest;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
// @CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn) {
        try {
            List<Book> books = bookService.searchBooks(title, author, isbn);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("検索に失敗しました: " + e.getMessage());
        }
    }

    @GetMapping("/borrowed/{userId}")
    public ResponseEntity<?> getBorrowedBooks(@PathVariable Long userId) {
        try {
            List<Book> books = bookService.getBorrowedBooks(userId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("借りている本の取得に失敗しました: " + e.getMessage());
        }
    }

    @GetMapping("/reserved/{userId}")
    public ResponseEntity<?> getReservedBooks(@PathVariable Long userId) {
        try {
            List<Book> books = bookService.getReservedBooks(userId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            System.out.println("予約本の取得エラー: " + e.getMessage());
            return ResponseEntity.ok(new ArrayList<>()); // エラー時は空のリストを返す
        }
    }

    @PostMapping
    public ResponseEntity<?> registerBook(@RequestBody BookRequest request) {
        try {
            bookService.registerBook(request);
            return ResponseEntity.ok()
                .body(new HashMap<String, Object>() {{
                    put("message", "書籍を登録しました");
                    put("success", true);
                }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new HashMap<String, Object>() {{
                    put("message", e.getMessage());
                    put("success", false);
                }});
        }
    }
} 