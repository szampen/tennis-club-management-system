package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService service) {
        this.reservationService = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUserReservations(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "You must be logged in."));
        }
        Long userId = ((com.tennis.dto.UserDTO) userObj).getId();

        return ResponseEntity.ok(reservationService.cancelReservation(id, userId));
    }
}
