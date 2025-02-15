package com.example.server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.server.Service.ReservationService;
import com.example.server.entity.Reservation;
import java.util.List;

@RestController
// @RequestMapping("/api/reservations")
// @CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/api/reservations")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            System.out.println(request.getUserId());
            System.out.println(request.getBookId());
            Reservation reservation = reservationService.createReservation(request.getUserId(), request.getBookId());
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("予約に失敗しました: " + e.getMessage());
        }
    }

    @GetMapping("/api/reservations/user/{userId}")
    public ResponseEntity<?> getUserReservations(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("予約情報の取得に失敗しました: " + e.getMessage());
        }
    }
}

class ReservationRequest {
    private Long userId;
    private Long bookId;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
} 