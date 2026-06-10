package com.dube.workflow.user;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dube.workflow.auth.Role;
import com.dube.workflow.auth.RoleRepository;
import com.dube.workflow.exception.BadRequestException;
import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.user.dto.RoleChangeRequestDTO;
import com.dube.workflow.user.dto.UserCreateRequestDTO;
import com.dube.workflow.user.dto.UserResponseDTO;
import com.dube.workflow.user.dto.UserUpdateRequestDTO;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
	@Transactional
	public UserResponseDTO createUser(UserCreateRequestDTO createRequest) {
	    if (userRepository.existsByEmail(createRequest.getEmail())) {
	        throw new BadRequestException("Email is already registered inside the system.");
	    }
	    
	    Role role = roleRepository.findByRoleName(createRequest.getRoleName())
	            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + createRequest.getRoleName()));
	            
	    User newUser = new User();
	    newUser.setFirstName(createRequest.getFirstName());
	    newUser.setLastName(createRequest.getLastName());
	    newUser.setEmail(createRequest.getEmail());
	    newUser.setMobile(createRequest.getMobile());
	    newUser.setPassword(passwordEncoder.encode(createRequest.getPassword())); 
	    newUser.setRole(role);
	    newUser.setStatus("ACTIVE"); 
	    
	    User savedUser = userRepository.save(newUser);
	    return mapToDTO(savedUser);
	}

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(String email, UserUpdateRequestDTO updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setMobile(updateRequest.getMobile());
        
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsersList() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO changeUserRole(UUID userId, RoleChangeRequestDTO roleChangeRequest, String adminEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                
        if (targetUser.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new BadRequestException("You cannot modify your own administrative role privileges.");
        }
        
        Role newRole = roleRepository.findByRoleName(roleChangeRequest.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleChangeRequest.getRoleName()));
                
        targetUser.setRole(newRole);
        User updatedUser = userRepository.save(targetUser);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID userId, String adminEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                
        if (targetUser.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new BadRequestException("You cannot deactivate your own administrative account.");
        }
        
        targetUser.setStatus("INACTIVE");
        userRepository.save(targetUser);
    }

    // 🚀 NEW: Explicitly brings an account state back to ACTIVE
    @Override
    @Transactional
    public UserResponseDTO activateUser(UUID userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        targetUser.setStatus("ACTIVE");
        User updatedUser = userRepository.save(targetUser);
        return mapToDTO(updatedUser);
    }

    private UserResponseDTO mapToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                user.getRole() != null ? user.getRole().getRoleName() : "NO_ROLE", 
                user.getStatus(),
                user.getCreatedAt(),
                user.getLastLogin()
        );
    }
}