package com.dube.workflow.user;

import com.dube.workflow.user.dto.RoleChangeRequestDTO;
import com.dube.workflow.user.dto.UserCreateRequestDTO;
import com.dube.workflow.user.dto.UserResponseDTO;
import com.dube.workflow.user.dto.UserUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
	UserResponseDTO createUser(UserCreateRequestDTO createRequest);
	UserResponseDTO activateUser(UUID userId);
    UserResponseDTO getUserProfileByEmail(String email);
    UserResponseDTO updateUserProfile(String email, UserUpdateRequestDTO updateRequest);
    List<UserResponseDTO> getAllUsersList();
    UserResponseDTO changeUserRole(UUID userId, RoleChangeRequestDTO roleChangeRequest, String adminEmail);
    void softDeleteUser(UUID userId, String adminEmail);
}