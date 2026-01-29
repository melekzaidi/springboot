package com.formation.controller.api;

import com.formation.dto.ApiResponseDTO;
import com.formation.dto.AuthRequestDTO;
import com.formation.dto.AuthResponseDTO;
import com.formation.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO request,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst().get().getAuthority().replace("ROLE_", "");

            String token = jwtUtils.generateToken(userDetails.getUsername(), role);

            // Set JWT in HttpOnly Cookie for web security
            Cookie cookie = new Cookie("AuthToken", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(cookie);

            AuthResponseDTO authResponse = new AuthResponseDTO(
                    token,
                    userDetails.getUsername(),
                    role,
                    "Connexion réussie"
            );

            return ResponseEntity.ok(ApiResponseDTO.success("Connexion réussie", authResponse));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.error("Email ou mot de passe incorrect"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout(HttpServletResponse response) {
        // Clear cookie
        Cookie cookie = new Cookie("AuthToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponseDTO.success("Déconnexion réussie", "OK"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.error("Non authentifié"));
        }

        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst().get().getAuthority().replace("ROLE_", "");

        AuthResponseDTO authResponse = new AuthResponseDTO(
                null, // Don't send token in response
                email,
                role,
                "Utilisateur authentifié"
        );

        return ResponseEntity.ok(ApiResponseDTO.success(authResponse));
    }
}