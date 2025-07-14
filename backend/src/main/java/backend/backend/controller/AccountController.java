package backend.backend.controller;

import backend.backend.dto.AccountDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.AccountService;
import backend.backend.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("@authService.isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AccountDto>> getProfile() {
        AccountDto accountDto = accountService.getAccountDetails(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseFactory.success(accountDto, "Get user profile success");
    }

    @PreAuthorize("@authService.isAuthenticated()")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(@RequestBody AccountDto accountDto) {
        AccountDto result = accountService.updateAccount(accountDto);
        return ResponseFactory.success(result, "Update user profile success");
    }
}
