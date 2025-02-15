package com.example.server.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.server.mapper.ReservationMapper;
import com.example.server.mapper.BookMapper;
import com.example.server.entity.Reservation;
import com.example.server.entity.Book;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private BookMapper bookMapper;

    @Transactional
    public Reservation createReservation(Long userId, Long bookId) {
        System.out.println("userId: " + userId);
        System.out.println("bookId: " + bookId);
        // 本の状態を確認
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new RuntimeException("指定された本が見つかりません");
        }
        if (!"貸出可能".equals(book.getStatus())) {
            throw new RuntimeException("この本は現在予約できません");
        }

        // 予約を作成
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setStatus("予約中");
        
        reservationMapper.insert(reservation);
        
        // 本の状態を更新
        book.setStatus("予約済み");
        bookMapper.updateStatus(bookId, "予約済み");

        return reservation;
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationMapper.findByUserId(userId);
    }
} 