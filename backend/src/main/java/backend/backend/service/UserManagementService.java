package backend.backend.service;

import backend.backend.dto.UserManagementDto;
import backend.backend.dto.UserCreateRequestDto;
import backend.backend.dto.UserUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    Page<UserManagementDto> getAllUsers(String search, String role, String status, Pageable pageable);
    UserManagementDto getUserById(Integer id);
    UserManagementDto createUser(UserCreateRequestDto request);
    UserManagementDto updateUser(Integer id, UserUpdateRequestDto request);
    UserManagementDto updateUserStatus(Integer id, String status);
    boolean deleteUser(Integer id);
    Object getUserStatistics();
}
