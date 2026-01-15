package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.dto.LoginRequest;
import com.tennis.dto.RegisterRequest;
import com.tennis.dto.UserDTO;
import com.tennis.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService service){
        this.userService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(){
        return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMe(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse> getPlayerStats(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPlayerStatistics(id));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/change-email")
    public ResponseEntity<ApiResponse> changeEmail(@RequestBody java.util.Map<String, String> body, @RequestParam Long userId) {
        return ResponseEntity.ok(userService.changeEmail(userId, body.get("email")));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody java.util.Map<String, String> body, @RequestParam Long userId) {
        return ResponseEntity.ok(userService.changePassword(userId, body.get("password")));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserDTO updates, @RequestParam Long userId) {
        ApiResponse response;

        if (updates.getFirstName() != null) response = userService.changeFirstName(userId, updates.getFirstName());
        else if (updates.getLastName() != null) response = userService.changeLastName(userId, updates.getLastName());
        else if (updates.getPhoneNumber() != null) response = userService.changePhoneNumber(userId, updates.getPhoneNumber());
        else return ResponseEntity.badRequest().body(new ApiResponse(false, "No valid field to update"));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<ApiResponse> deleteAccount(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
