package com.vdmcreation.TradeAlert.service;

import com.vdmcreation.TradeAlert.dto.UserDTO;
import com.vdmcreation.TradeAlert.dto.UserRequestDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO createUser(UserRequestDTO request);

    UserDTO updateUser(Long id, UserRequestDTO request);

    void deleteUser(Long id);
}
