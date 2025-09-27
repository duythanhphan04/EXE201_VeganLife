package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.UserCreationRequest;
import com.devteria.identity_service.dto.UserUpdateRequest;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.enums.UserPlan;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.mapper.UserMapper;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.response.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new WebException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User newUser = userMapper.toUser(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(Role.USER);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setCreatedAt(Instant.now());
        newUser.setPlan(UserPlan.FREE);
        userRepository.save(newUser);
        return userMapper.toUserResponse(newUser);
    }
    public UserResponse createCoach(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new WebException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User newUser = userMapper.toUser(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(Role.COACH);
        newUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(newUser);
        return userMapper.toUserResponse(newUser);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public User getUserEntityByID(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getUserByID(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND)));
    }
    public UserResponse getUserByUsername(String username) {
        return userMapper.toUserResponse(userRepository.findByUsername(username)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND)));
    }
    public UserResponse getUserByEmail(String email) {
        return userMapper.toUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND)));
    }
    public UserResponse updateUser(String userID, UserUpdateRequest request) {
        User user = userRepository.findById(userID)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setImg(request.getImg());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    public User getLoggedInUser(){
        var context = SecurityContextHolder.getContext();
        String name = Objects.requireNonNull(context.getAuthentication()).getName();
        return userRepository.findByUsername(name)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
    }
    public UserResponse deleteUser(String id) {
        User u = userRepository.findById(id)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(u);
        return userMapper.toUserResponse(u);
    }
    public UserResponse updateUserStatus(String id , UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(status);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    public UserResponse updateUserCoach (String userID , String coachID){
        User user = userRepository.findById(userID)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
        User coach = userRepository.findById(coachID)
                .orElseThrow(() ->  new WebException(ErrorCode.COACH_NOT_FOUND));
        if(!(coach.getRole() == Role.COACH)){
            throw new WebException(ErrorCode.INVALID_ROLE);
        }
        user.setCoach(coach);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return userMapper.toUserResponse(userRepository.findByUsername(name)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND)));
    }
    public List<UserResponse> getUserByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream().map(userMapper::toUserResponse).toList();
    }
    public List<UserResponse> getUserByRole(Role role) {
        return userRepository.findByRole(role).stream().map(userMapper::toUserResponse).toList();
    }
    public List<UserResponse> getUserByCoach(String coachId) {
        return userRepository.findByCoachUserID(coachId).stream().map(userMapper::toUserResponse).toList();
    }
    public String getLoggedInUsername() {
        var context = SecurityContextHolder.getContext();
        return Objects.requireNonNull(context.getAuthentication()).getName();
    }

    public User getUserEntity(String loginUsername) {
        return userRepository.findByUsername(loginUsername)
                .orElseThrow(() ->  new WebException(ErrorCode.USER_NOT_FOUND));
    }

}
