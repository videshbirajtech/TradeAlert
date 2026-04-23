package com.vdmcreation.TradeAlert.service.impl;

import com.vdmcreation.TradeAlert.dto.UserDTO;
import com.vdmcreation.TradeAlert.dto.UserRequestDTO;
import com.vdmcreation.TradeAlert.entity.User;
import com.vdmcreation.TradeAlert.repository.UserRepository;
import com.vdmcreation.TradeAlert.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id)));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return toDTO(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email)));
    }

    @Override
    public UserDTO createUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.getEmail());
        }
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail());
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        applyUpdate(user, request);
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUserByEmail(String email, UserRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
        applyUpdate(user, request);
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO uploadProfilePhoto(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));

        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Delete old photo if exists
            if (user.getProfilePhoto() != null) {
                Path oldFile = Paths.get(uploadDir, user.getProfilePhoto());
                Files.deleteIfExists(oldFile);
            }

            // Save new file with unique name
            String ext = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));

            user.setProfilePhoto(fileName);
            return toDTO(userRepository.save(user));

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private void applyUpdate(User user, UserRequestDTO request) {
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.getEmail());
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCreatedDate(user.getCreatedDate());
        // Build full URL for profile photo
        if (user.getProfilePhoto() != null) {
            dto.setProfilePhoto("/uploads/" + user.getProfilePhoto());
        }
        return dto;
    }
}
