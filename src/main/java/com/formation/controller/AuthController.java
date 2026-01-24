package com.formation.controller;

import com.formation.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("email") String email, 
                          @RequestParam("password") String password, 
                          HttpServletResponse response, 
                          Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst().get().getAuthority().replace("ROLE_", "");

            String token = jwtUtils.generateToken(userDetails.getUsername(), role);

            // Set JWT in HttpOnly Cookie
            Cookie cookie = new Cookie("AuthToken", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(cookie);

            // Redirect based on role
            if ("ADMIN".equals(role)) {
                return "redirect:/"; // Admin sees the home page / dashboard
            } else {
                return "redirect:/calendar"; // Students/Profs see their calendar
            }

        } catch (AuthenticationException e) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Clear cookie
        Cookie cookie = new Cookie("AuthToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
