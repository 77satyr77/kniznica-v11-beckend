package org.example.kniznica_11.controller;

import org.example.kniznica_11.dto.ReqRes;
import org.example.kniznica_11.entity.Users;
import org.example.kniznica_11.service.MyUsersManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyUserManagementController {

    private final MyUsersManagementService myUsersManagementService;

    public MyUserManagementController(MyUsersManagementService myUsersManagementService) {
        this.myUsersManagementService = myUsersManagementService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reqRes) {

        return ResponseEntity.ok(myUsersManagementService.register(reqRes));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(myUsersManagementService.login(reqRes));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(myUsersManagementService.getAllUsers());
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refresh(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(myUsersManagementService.refreshToken(reqRes));
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(myUsersManagementService.getUserById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Long userId, @RequestBody Users reqres) {
        return ResponseEntity.ok(myUsersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/user/get-profile")
    public ResponseEntity<ReqRes> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ReqRes response = myUsersManagementService.getMyInfo(username);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(myUsersManagementService.deleteUser(userId));
    }
}
