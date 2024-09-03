package com.pccw.usermanagementsystem.controller;

import com.pccw.usermanagementsystem.dto.UpdateUserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserResponseDTO;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.mapper.UserMapper;
import com.pccw.usermanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;



    @Operation(summary = "Register a new user", description = "This endpoint allows you to register a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exist"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid  @RequestBody  UserRequestDTO userRequestDTO) {
        User user = userService.registerUser(userMapper.toEntity(userRequestDTO));
        return ResponseEntity.status(201).body(userMapper.toDTO(user));
    }

    @PostMapping("/register/bulk")
    @Operation(summary = "Register multiple users", description = "This endpoint allows the registration of multiple users in a single request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Users registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "409", description = "One or more users already exist")
    })
    public ResponseEntity<List<UserResponseDTO>> registerUsers(@Valid @RequestBody  List<@NotNull UserRequestDTO> userRequestDTOs) {
        List<User> users = userService.registerUsers(userRequestDTOs.stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toList()));

        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.status(201).body(userResponseDTOs);
    }

    @Operation(summary = "Get a user by ID", description = "This endpoint returns a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id")  @NotNull  Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID path variable cannot be null");
        }
        User user = userService.findActiveUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @Operation(summary = "Get all users", description = "This endpoint returns a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
    })
    @GetMapping("/bulk")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    @Operation(summary = "Update a user", description = "This endpoint allows you to update an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @NotNull @Valid UpdateUserRequestDTO userRequestDTO) {
        User updatedUser = userService.updateUser( userMapper.toEntity(userRequestDTO));
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @Operation(summary = "Bulk update multiple users", description = "This endpoint allows you to update multiple users in one request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users updated successfully"),
            @ApiResponse(responseCode = "404", description = "One or more users not found")
    })
    @PutMapping("/bulk-update")
    public ResponseEntity<List<UserResponseDTO>> updateUsers(@RequestBody  @NotNull  List<@NotNull @Valid UpdateUserRequestDTO> userRequestDTOs) {
        List<User> updatedUsers = userService.updateUsers(userRequestDTOs.stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toList()));

        List<UserResponseDTO> userResponseDTOs = updatedUsers.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponseDTOs);
    }

    @Operation(summary = "Soft delete a user", description = "This endpoint allows you to soft delete a user by setting their status to inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteUser(@PathVariable @NotNull Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Soft delete multiple users", description = "This endpoint allows you to soft delete multiple users by setting their status to inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User soft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/bulk-soft-delete")
    public ResponseEntity<Void> deleteUsers(@RequestBody @NotNull @Valid List<Long> ids) {
        userService.softDeleteUsers(ids);
        return ResponseEntity.noContent().build();
    }
}
