package com.tennis.controller;

import com.tennis.domain.User;
import com.tennis.dto.ApiResponse;
import com.tennis.dto.CourtDTO;
import com.tennis.service.CourtService;
import com.tennis.util.CourtFilter;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse createCourt(@RequestBody CourtDTO courtDTO, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return new ApiResponse(false, "Not logged in");

        Long adminId = ((com.tennis.dto.UserDTO) user).getId();
        return courtService.createCourt(courtDTO, adminId);
    }

    @PutMapping("/{id}/update")
    public ApiResponse updateCourt(@PathVariable Long id, @RequestBody CourtDTO updates, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return new ApiResponse(false, "Not logged in");

        Long adminId = ((com.tennis.dto.UserDTO) userObj).getId();
        boolean updatedAny = false;

        if (updates.getName() != null) {
            courtService.changeName(id,adminId, updates.getName());
            updatedAny = true;
        }
        if (updates.getCourtNumber() != null && updates.getCourtNumber() != 0) {
            courtService.changeCourtNumber(id,adminId, updates.getCourtNumber());
            updatedAny = true;
        }
        if (updates.getLocation() != null) {
            courtService.changeLocation(id, adminId, updates.getLocation());
            updatedAny = true;
        }
        if (updates.getPricePerHour() != null && updates.getPricePerHour() > 0) {
            courtService.changePricePerHour(id, adminId, updates.getPricePerHour());
            updatedAny = true;
        }
        if (updates.getSurfaceType() != null) {
            courtService.changeSurfaceType(id, adminId, updates.getSurfaceType());
            updatedAny = true;
        }
        if (updates.isHasRoof() != null) {
            courtService.changeHasRoof(id, adminId, updates.isHasRoof());
            updatedAny = true;
        }
        if (updates.isAvailableForReservations() != null) {
            courtService.changeAvailableForReservations(id, adminId, updates.isAvailableForReservations());
            updatedAny = true;
        }

        if (updatedAny) {
            return new ApiResponse(true, "Court updated successfully");
        } else {
            return new ApiResponse(false, "Nothing to update");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteCourt(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return new ApiResponse(false, "Not logged in");
        }
        Long userId = ((com.tennis.dto.UserDTO) user).getId();
        return courtService.deleteCourt(id, userId);
    }
}
