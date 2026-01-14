package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.dto.CreateReservationRequest;
import com.tennis.service.ReservationService;
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
    public ResponseEntity<ApiResponse> getDetails(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(reservationService.getReservation(id,userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelReservation(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, userId));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createReservation(@RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }
}
