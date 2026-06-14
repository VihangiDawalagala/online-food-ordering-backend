package com.example.foodorder.service;

import com.example.foodorder.dto.*;
import com.example.foodorder.entity.Role;
import com.example.foodorder.entity.User;
import com.example.foodorder.repository.UserRepository;
import com.example.foodorder.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
        log.info("New customer registered with email {}", user.getEmail());

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                user.getId(),
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }

    public AuthResponse signIn(SignInRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for email {}", request.getEmail());
            throw ex;
        }

        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token = jwtUtil.generateToken(userDetails);
        log.info("User signed in with email {}", user.getEmail());

        return new AuthResponse(
                user.getId(),
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
    }
}
