package com.vdmcreation.TradeAlert.controller;

import com.vdmcreation.TradeAlert.dto.UserDTO;
import com.vdmcreation.TradeAlert.dto.UserRequestDTO;
import com.vdmcreation.TradeAlert.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // GET /api/users/profile?email=user@example.com
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // PUT /api/users/profile?email=user@example.com
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUserByEmail(@RequestParam String email,
                                                     @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.updateUserByEmail(email, request));
    }

    // POST /api/users/profile/photo?email=user@example.com
    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> uploadProfilePhoto(@RequestParam String email,
                                                      @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePhoto(email, file));
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
