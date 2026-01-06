package com.tennis.controller;

import com.tennis.dto.ApiResponse;
import com.tennis.dto.LoginRequest;
import com.tennis.dto.RegisterRequest;
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
}
