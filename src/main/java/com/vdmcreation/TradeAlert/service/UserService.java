package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.UserDTO;
import com.vdmcreation.TradeAlert.dto.UserRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    UserDTO createUser(UserRequestDTO request);

    UserDTO updateUser(Long id, UserRequestDTO request);

    UserDTO updateUserByEmail(String email, UserRequestDTO request);

    UserDTO uploadProfilePhoto(String email, MultipartFile file);

    void deleteUser(Long id);
}
