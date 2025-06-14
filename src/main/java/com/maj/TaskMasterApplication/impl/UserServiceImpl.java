package com.maj.TaskMasterApplication.impl;

import com.maj.TaskMasterApplication.dto.AuthResponseDto;
import com.maj.TaskMasterApplication.dto.LoginRequestDto;
import com.maj.TaskMasterApplication.dto.SignUpRequestDto;
import com.maj.TaskMasterApplication.dto.UserResponseDto;
import com.maj.TaskMasterApplication.exception.BadRequestException;
import com.maj.TaskMasterApplication.exception.ResourceNotFoundException;
import com.maj.TaskMasterApplication.model.User;
import com.maj.TaskMasterApplication.model.Roles;
import com.maj.TaskMasterApplication.repository.UserRepository;
import com.maj.TaskMasterApplication.security.JwtTokenProvider; // Import
import com.maj.TaskMasterApplication.service.UserService;
import com.maj.TaskMasterApplication.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager; // Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Import
import org.springframework.security.core.Authentication; // Import
import org.springframework.security.core.context.SecurityContextHolder; // Import
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Now used
    private final JwtTokenProvider jwtTokenProvider;       // Now used

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, // Injected
                           JwtTokenProvider jwtTokenProvider) {         // Injected
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponseDto registerUser(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequestDto.getUsername());
        user.setEmail(signUpRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setRole(signUpRequestDto.getRole() != null ? signUpRequestDto.getRole() : Roles.USER);

        User savedUser = userRepository.save(user);

        // Generate token for the newly registered user (optional to auto-login)
        // To do this, we can create an Authentication object manually or rely on the login method
        // For simplicity, let's generate token directly from user object
        String token = jwtTokenProvider.generateToken(savedUser); // Using the overloaded method

        return new AuthResponseDto(token, savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    @Override
    public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getLoginIdentifier(), // This is passed to CustomUserDetailsService
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        // UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // This is our User entity
        User userPrincipal = (User) authentication.getPrincipal();


        return new AuthResponseDto(jwt, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getRole());
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return DtoMapper.toUserResponseDto(user);
    }
}