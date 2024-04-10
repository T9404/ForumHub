package com.example.rest.auth.v1;

import com.example.core.auth.AuthenticationService;
import com.example.rest.admin.v1.request.CreateUserDto;
import com.example.rest.auth.v1.request.LoginRequest;
import com.example.rest.auth.v1.request.RefreshTokenRequest;
import com.example.rest.auth.v1.request.ResendTokenRequest;
import com.example.rest.auth.v1.response.JwtResponse;
import com.example.rest.auth.v1.response.RegistrationResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public RegistrationResponse signUp(@RequestBody @Valid CreateUserDto request) {
        return authService.signUp(request);
    }

    @GetMapping("/verification")
    public void confirmEmail(@RequestParam String token) {
        authService.confirmEmail(token);
    }

    @PostMapping("/resend-token")
    public void resendToken(@RequestBody @Valid ResendTokenRequest request) {
        authService.resendToken(request);
    }

    @PostMapping("/login")
    public JwtResponse signIn(@RequestBody @Valid LoginRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public void signOut(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
    }
}
