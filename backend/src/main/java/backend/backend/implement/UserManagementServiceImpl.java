package backend.backend.implement;

import backend.backend.dto.UserManagementDto;
import backend.backend.dto.UserCreateRequestDto;
import backend.backend.dto.UserUpdateRequestDto;
import backend.backend.entity.User;
import backend.backend.entity.UserRole;
import backend.backend.entity.Role;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.repository.UserRepository;
import backend.backend.repository.RoleRepository;
import backend.backend.repository.UserRoleRepository;
import backend.backend.service.UserManagementService;
import backend.backend.dto.auth.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserManagementDto> getAllUsers(String search, String role, String status, Pageable pageable) {
        // Simple filtering without Specification for now
        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> {
                if (StringUtils.hasText(search)) {
                    String searchLower = search.toLowerCase();
                    return user.getName().toLowerCase().contains(searchLower) ||
                           user.getEmail().toLowerCase().contains(searchLower) ||
                           (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchLower));
                }
                return true;
            })
            .filter(user -> {
                if (StringUtils.hasText(status)) {
                    if ("ACTIVE".equals(status)) {
                        return Boolean.TRUE.equals(user.getIsVerified());
                    } else if ("INACTIVE".equals(status)) {
                        return Boolean.FALSE.equals(user.getIsVerified());
                    }
                }
                return true;
            })
            .collect(Collectors.toList());

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredUsers.size());
        List<User> pageContent = filteredUsers.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredUsers.size())
            .map(this::mapToUserManagementDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementDto getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserManagementDto(user);
    }

    @Override
    @Transactional
    public UserManagementDto createUser(UserCreateRequestDto request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender() != null ? 
            backend.backend.dto.GenderEnum.valueOf(request.getGender()) : null);
        user.setBirthday(request.getBirthday());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsVerified(true); // Default to verified
        user.setAuthProvider(backend.backend.dto.auth.AuthProvider.INTERNAL);

        user = userRepository.save(user);

        // Assign roles - ưu tiên field roles nếu có, fallback về field role
        List<String> rolesToAssign = new ArrayList<>();
        
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Sử dụng field roles nếu có
            rolesToAssign.addAll(request.getRoles());
        } else if (StringUtils.hasText(request.getRole())) {
            // Fallback về field role nếu roles không có
            rolesToAssign.add(request.getRole());
        } else {
            throw new RuntimeException("Either roles or role must be provided");
        }
        
        // Assign tất cả roles
        for (String roleName : rolesToAssign) {
            Role role = roleRepository.findByName(UserRoleEnum.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setId(new backend.backend.entity.UserRoleId(user.getId(), role.getId()));
            userRoleRepository.save(userRole);
        }

        return mapToUserManagementDto(user);
    }

    @Override
    @Transactional
    public UserManagementDto updateUser(Integer id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(backend.backend.dto.GenderEnum.valueOf(request.getGender()));
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (StringUtils.hasText(request.getStatus())) {
            // Map status to isVerified
            if ("ACTIVE".equals(request.getStatus())) {
                user.setIsVerified(true);
            } else if ("INACTIVE".equals(request.getStatus())) {
                user.setIsVerified(false);
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Remove existing roles
            List<UserRole> existingRoles = userRoleRepository.findAll().stream()
                .filter(ur -> ur.getUser().getId().equals(id))
                .collect(Collectors.toList());
            userRoleRepository.deleteAll(existingRoles);
            
            // Add new roles
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(UserRoleEnum.valueOf(roleName))
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRole.setId(new backend.backend.entity.UserRoleId(user.getId(), role.getId()));
                userRoleRepository.save(userRole);
            }
        }

        return mapToUserManagementDto(user);
    }

    @Override
    @Transactional
    public UserManagementDto updateUserStatus(Integer id, String status) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if ("ACTIVE".equals(status)) {
            user.setIsVerified(true);
        } else if ("INACTIVE".equals(status)) {
            user.setIsVerified(false);
        }
        // BANNED status would need additional field

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        return mapToUserManagementDto(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Xóa tất cả roles của user trước
        List<UserRole> existingRoles = userRoleRepository.findAll().stream()
            .filter(ur -> ur.getUser().getId().equals(id))
            .collect(Collectors.toList());
        userRoleRepository.deleteAll(existingRoles);
        
        // Xóa user
        userRepository.delete(user);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream()
            .filter(user -> Boolean.TRUE.equals(user.getIsVerified()))
            .count();
        long inactiveUsers = userRepository.findAll().stream()
            .filter(user -> Boolean.FALSE.equals(user.getIsVerified()))
            .count();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("activeUsers", activeUsers);
        statistics.put("inactiveUsers", inactiveUsers);
        statistics.put("bannedUsers", 0L); // Would need additional field

        return statistics;
    }

    private UserManagementDto mapToUserManagementDto(User user) {
        UserManagementDto dto = new UserManagementDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setBirthday(user.getBirthday());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsVerified(user.getIsVerified());
        
        // Map status based on isVerified
        if (user.getIsVerified() != null) {
            dto.setStatus(user.getIsVerified() ? "ACTIVE" : "INACTIVE");
        } else {
            dto.setStatus("INACTIVE");
        }

        // Map roles
        if (user.getUserRoles() != null) {
            List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName().name())
                .collect(Collectors.toList());
            dto.setRoles(roles);
        }

        // TODO: Calculate totalBookings and totalSpent from related entities
        dto.setTotalBookings(0);
        dto.setTotalSpent(0L);

        return dto;
    }
}
