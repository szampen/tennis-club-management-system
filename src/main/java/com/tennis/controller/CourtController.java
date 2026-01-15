package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.dto.CourtDTO;
import com.tennis.service.CourtService;
import com.tennis.util.CourtFilter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/courts")
public class CourtController {
    private final CourtService courtService;

    public CourtController(CourtService service) {
        this.courtService = service;
    }

    @PostMapping("/filtered")
    public ResponseEntity<ApiResponse> getFiltered(@RequestBody CourtFilter filter) {
        return ResponseEntity.ok(courtService.getCourtsFiltered(filter));
    }

    @GetMapping("/{id}")
    public ApiResponse getCourt(@PathVariable Long id) {
        ApiResponse response = courtService.getCourt(id);
        CourtDTO dto = (CourtDTO) response.getData();
        if (dto != null) {
            return new ApiResponse(true, "Court found", dto);
        }
        return new ApiResponse(false, "Court not found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCourt(@RequestBody CourtDTO courtDTO, @RequestParam Long userId) {
        return ResponseEntity.ok(courtService.createCourt(courtDTO, userId));
    }

    @PutMapping("/{id}/update")
    public ApiResponse updateCourt(@PathVariable Long id, @RequestBody CourtDTO updates, @RequestParam Long userId) {
        boolean updatedAny = false;

        if (updates.getName() != null) {
            ApiResponse res = courtService.changeName(id,userId, updates.getName());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.getCourtNumber() != null && updates.getCourtNumber() != 0) {
            ApiResponse res = courtService.changeCourtNumber(id,userId, updates.getCourtNumber());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.getLocation() != null) {
            ApiResponse res = courtService.changeLocation(id, userId, updates.getLocation());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.getPricePerHour() != null && updates.getPricePerHour() > 0) {
            ApiResponse res = courtService.changePricePerHour(id, userId, updates.getPricePerHour());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.getSurfaceType() != null) {
            ApiResponse res = courtService.changeSurfaceType(id, userId, updates.getSurfaceType());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.isHasRoof() != null) {
            ApiResponse res = courtService.changeHasRoof(id, userId, updates.isHasRoof());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }
        if (updates.isAvailableForReservations() != null) {
            ApiResponse res = courtService.changeAvailableForReservations(id, userId, updates.isAvailableForReservations());
            if(!res.isSuccess()) return res;
            updatedAny = true;
        }

        if (updatedAny) {
            return new ApiResponse(true, "Court updated successfully");
        } else {
            return new ApiResponse(false, "Nothing to update");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCourt(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(courtService.deleteCourt(id, userId));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse> getAvailability(@PathVariable Long id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(courtService.getCourtAvailability(id, date,null));
    }
}
