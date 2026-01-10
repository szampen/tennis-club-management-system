package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.dto.LoginRequest;
import com.tennis.dto.RegisterRequest;
import com.tennis.dto.UserDTO;
import com.tennis.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request, HttpSession session){
        ApiResponse response = userService.login(request);
        if(response.isSuccess()){
            session.setAttribute("user",response.getData());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session){
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMe(HttpSession session){
        Object user = session.getAttribute("user");
        if(user == null){
            return ResponseEntity.status(401).body(new ApiResponse(false, "Not logged in."));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Active session.", user));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse> getPlayerStats(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPlayerStatistics(id));
    }

    @PutMapping("/change-email")
    public ResponseEntity<ApiResponse> changeEmail(@RequestBody java.util.Map<String, String> body, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized"));

        Long userId = ((com.tennis.dto.UserDTO) userObj).getId();
        ApiResponse resp = userService.changeEmail(userId, body.get("email"));
        if(resp.isSuccess()) session.invalidate();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody java.util.Map<String, String> body, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized"));

        Long userId = ((com.tennis.dto.UserDTO) userObj).getId();
        ApiResponse resp = userService.changePassword(userId, body.get("password"));
        if(resp.isSuccess()) session.invalidate();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserDTO updates, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized"));

        Long userId = ((com.tennis.dto.UserDTO) userObj).getId();
        ApiResponse response;

        if (updates.getFirstName() != null) response = userService.changeFirstName(userId, updates.getFirstName());
        else if (updates.getLastName() != null) response = userService.changeLastName(userId, updates.getLastName());
        else if (updates.getPhoneNumber() != null) response = userService.changePhoneNumber(userId, updates.getPhoneNumber());
        else return ResponseEntity.badRequest().body(new ApiResponse(false, "No valid field to update"));

        if (response.isSuccess()) {
            session.setAttribute("user", userService.getUser(userId).getData());
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<ApiResponse> deleteAccount(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized"));

        Long userId = ((com.tennis.dto.UserDTO) userObj).getId();
        ApiResponse resp = userService.deleteUser(userId);
        if(resp.isSuccess()) session.invalidate();
        return ResponseEntity.ok(resp);
    }
}
