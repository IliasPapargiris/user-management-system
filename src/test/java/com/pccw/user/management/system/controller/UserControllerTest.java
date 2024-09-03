package com.pccw.user.management.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pccw.user.management.system.fixtures.UpdateUserRequestDTOFixture;
import com.pccw.user.management.system.fixtures.UserFixture;
import com.pccw.user.management.system.fixtures.UserRequestDTOFixture;
import com.pccw.user.management.system.fixtures.UserResponseDTOFixture;
import com.pccw.usermanagementsystem.UserManagementSystemApplication;
import com.pccw.usermanagementsystem.dto.UpdateUserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserResponseDTO;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.exception.UserNotFoundException;
import com.pccw.usermanagementsystem.mapper.UserMapper;
import com.pccw.usermanagementsystem.repository.UserRepository;
import com.pccw.usermanagementsystem.security.CustomUserDetailsService;
import com.pccw.usermanagementsystem.service.UserService;
import com.pccw.usermanagementsystem.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = UserManagementSystemApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUserSuccessfully() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = UserRequestDTOFixture.getInstance().getDefaultUserRequestDTO();
        User user = UserFixture.getInstance().getDefaultUser();
        UserResponseDTO userResponseDTO = UserResponseDTOFixture.getInstance().getDefaultUserResponseDTO();

        when(userRepository.findByUsername(userRequestDTO.getUsername())).thenReturn(Optional.of(user));

        UserDetails userDetails = withUsername(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .roles(userRequestDTO.getRole())
                .build();


        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userService.registerUser(user)).thenReturn(user);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO.getId()))  // Correct jsonPath import
                .andExpect(jsonPath("$.username").value(userResponseDTO.getUsername()))
                .andExpect(jsonPath("$.role").value(userResponseDTO.getRole()))  // Assuming 'role' is a string
                .andExpect(jsonPath("$.enabled").value(userResponseDTO.getEnabled()));
    }

    @Test
    void testBulkRegisterUsersSuccessfully() throws Exception {
        // Given
        List<UserRequestDTO> userRequestDTOs = Arrays.asList(
                UserRequestDTOFixture.getInstance().getAdminUserRequestDTO(),
                UserRequestDTOFixture.getInstance().getDefaultUserRequestDTO()
        );

        List<User> users = Arrays.asList(
                UserFixture.getInstance().getAdminUser(),
                UserFixture.getInstance().getDefaultUser()
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                UserResponseDTOFixture.getInstance().getAdminUserResponseDTO(),
                UserResponseDTOFixture.getInstance().getDefaultUserResponseDTO()
        );

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty()); // Ensure no user already exists

        when(userMapper.toEntity(any(UserRequestDTO.class)))
                .thenReturn(users.get(0))  // Map the first DTO to the first User entity
                .thenReturn(users.get(1)); // Map the second DTO to the second User entity

        when(userService.registerUsers(anyList())).thenReturn(users);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());

        when(userMapper.toDTO(any(User.class)))
                .thenReturn(userResponseDTOs.get(0))  // Map the first User entity to the first DTO
                .thenReturn(userResponseDTOs.get(1)); // Map the second User entity to the second DTO

        UserDetails adminUserDetails = org.springframework.security.core.userdetails.User.withUsername("admin@example.com")
                .password("adminPassword") // You can use a plain password or encoded, depending on your configuration
                .roles("ADMIN")
                .build();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTOs))
                        .with(SecurityMockMvcRequestPostProcessors.user(adminUserDetails)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userResponseDTOs.get(0).getId())) // Admin User
                .andExpect(jsonPath("$[0].username").value(userResponseDTOs.get(0).getUsername()))
                .andExpect(jsonPath("$[0].role").value(userResponseDTOs.get(0).getRole()))
                .andExpect(jsonPath("$[0].enabled").value(userResponseDTOs.get(0).getEnabled()))
                .andExpect(jsonPath("$[1].id").value(userResponseDTOs.get(1).getId())) // Default User
                .andExpect(jsonPath("$[1].username").value(userResponseDTOs.get(1).getUsername()))
                .andExpect(jsonPath("$[1].role").value(userResponseDTOs.get(1).getRole()))
                .andExpect(jsonPath("$[1].enabled").value(userResponseDTOs.get(1).getEnabled()));
    }

    @Test
    void testGetUserByIdSuccessfully() throws Exception {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();
        UserResponseDTO userResponseDTO = UserResponseDTOFixture.getInstance().getDefaultUserResponseDTO();

        when(userService.findActiveUserById(user.getId())).thenReturn(user);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());
        when(userMapper.toDTO(user)).thenReturn(userResponseDTO);

        UserDetails userDetails = withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        // When & Then
        mockMvc.perform(get("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO.getId()))
                .andExpect(jsonPath("$.username").value(userResponseDTO.getUsername()))
                .andExpect(jsonPath("$.role").value(userResponseDTO.getRole()))
                .andExpect(jsonPath("$.enabled").value(userResponseDTO.getEnabled()));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // Given
        Long nonExistentUserId = 1L;
        User defaultUser = UserFixture.getInstance().getDefaultUser();

        when(userService.findActiveUserById(nonExistentUserId)).thenThrow(new UserNotFoundException(nonExistentUserId));
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());

        UserDetails userDetails = withUsername(defaultUser.getUsername())
                .password(defaultUser.getPassword())
                .roles(defaultUser.getRole().name())
                .build();

        // When & Then
        mockMvc.perform(get("/api/users/{id}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserSuccessfully() throws Exception {
        // Given
        UpdateUserRequestDTO userRequestDTO = UpdateUserRequestDTOFixture.getInstance().getDefaultUpdateUserRequestDTO();
        User user = UserFixture.getInstance().getDefaultUser();
        User updatedUser = new User(user.getId(), "updated", user.getPassword(), user.getRole(), user.getEnabled());
        UserResponseDTO userResponseDTO = UserResponseDTOFixture.getInstance().getDefaultUserResponseDTO();
        userResponseDTO.setUsername("updated");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.updateUser(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toEntity(userRequestDTO)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(userResponseDTO);

        UserDetails userDetails = withUsername(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .roles(userRequestDTO.getRole())
                .build();

        // Mock the EmailService to do nothing when sendWelcomeEmail is called
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO.getId()))
                .andExpect(jsonPath("$.username").value(userResponseDTO.getUsername()))
                .andExpect(jsonPath("$.role").value(userResponseDTO.getRole()))
                .andExpect(jsonPath("$.enabled").value(userResponseDTO.getEnabled()));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        // Given
        UpdateUserRequestDTO userRequestDTO = UpdateUserRequestDTOFixture.getInstance().getDefaultUpdateUserRequestDTO();
        User user = UserFixture.getInstance().getDefaultUser();
        long nonExistentUserId = 1L;

//        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userService.updateUser(user)).thenThrow(new UserNotFoundException(nonExistentUserId));

        UserDetails userDetails = withUsername(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .roles(userRequestDTO.getRole())
                .build();

        // When & Then
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBulkUpdateUsersSuccessfully() throws Exception {
        // Given
        List<UpdateUserRequestDTO> userRequestDTOs = Arrays.asList(
                UpdateUserRequestDTOFixture.getInstance().getAdminUpdateUserRequestDTO(),
                UpdateUserRequestDTOFixture.getInstance().getDefaultUpdateUserRequestDTO()
        );

        List<User> users = Arrays.asList(
                UserFixture.getInstance().getAdminUser(),
                UserFixture.getInstance().getDefaultUser()
        );

        List<User> updatedUsers = Arrays.asList(
                new User(users.get(0).getId(), "updatedAdmin", users.get(0).getPassword(), users.get(0).getRole(), users.get(0).getEnabled()),
                new User(users.get(1).getId(), "updatedDefault", users.get(1).getPassword(), users.get(1).getRole(), users.get(1).getEnabled())
        );

        List<UserResponseDTO> userResponseDTOs = Arrays.asList(
                new UserResponseDTO(updatedUsers.get(0).getId(), "updatedAdmin", updatedUsers.get(0).getRole().name(), updatedUsers.get(0).getEnabled()),
                new UserResponseDTO(updatedUsers.get(1).getId(), "updatedDefault", updatedUsers.get(1).getRole().name(), updatedUsers.get(1).getEnabled())
        );

        when(userRepository.findById(users.get(0).getId())).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(users.get(1).getId())).thenReturn(Optional.of(users.get(1)));
        when(userMapper.toEntity(userRequestDTOs.get(0))).thenReturn(updatedUsers.get(0));
        when(userMapper.toEntity(userRequestDTOs.get(1))).thenReturn(updatedUsers.get(1));
        when(userService.updateUsers(anyList())).thenReturn(updatedUsers);
        when(userMapper.toDTO(updatedUsers.get(0))).thenReturn(userResponseDTOs.get(0));
        when(userMapper.toDTO(updatedUsers.get(1))).thenReturn(userResponseDTOs.get(1));

        UpdateUserRequestDTO userResponseDTO = userRequestDTOs.getFirst();
        UserDetails adminUserDetails = withUsername(userResponseDTO.getUsername())
                .password(userResponseDTO.getPassword()) // Use a plain or encoded password depending on your configuration
                .roles(userResponseDTO.getRole())
                .build();


        // When & Then
        mockMvc.perform(put("/api/users/bulk-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTOs))
                        .with(SecurityMockMvcRequestPostProcessors.user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userResponseDTOs.get(0).getId())) // Admin User
                .andExpect(jsonPath("$[0].username").value(userResponseDTOs.get(0).getUsername()))
                .andExpect(jsonPath("$[0].role").value(userResponseDTOs.get(0).getRole()))
                .andExpect(jsonPath("$[0].enabled").value(userResponseDTOs.get(0).getEnabled()))
                .andExpect(jsonPath("$[1].id").value(userResponseDTOs.get(1).getId())) // Default User
                .andExpect(jsonPath("$[1].username").value(userResponseDTOs.get(1).getUsername()))
                .andExpect(jsonPath("$[1].role").value(userResponseDTOs.get(1).getRole()))
                .andExpect(jsonPath("$[1].enabled").value(userResponseDTOs.get(1).getEnabled()));
    }

    @Test
    void testBulkUpdateUsersUnsuccessfully() throws Exception {
        // Given
        List<UpdateUserRequestDTO> userRequestDTOs = Arrays.asList(
                UpdateUserRequestDTOFixture.getInstance().getAdminUpdateUserRequestDTO(),
                UpdateUserRequestDTOFixture.getInstance().getDefaultUpdateUserRequestDTO()
        );

        List<User> users = Arrays.asList(
                UserFixture.getInstance().getAdminUser(),
                UserFixture.getInstance().getDefaultUser()
        );

        // Simulate that the second user does not exist in the database
        when(userRepository.findById(users.get(0).getId())).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(users.get(1).getId())).thenReturn(Optional.empty());

        // Expect the service method to throw an exception when it tries to update the non-existent user
        when(userService.updateUsers(anyList())).thenThrow(new UserNotFoundException(users.get(1).getId()));

        UserDetails adminUserDetails = withUsername("admin@example.com")
                .password("adminPassword") // Use a plain or encoded password depending on your configuration
                .roles("ADMIN")
                .build();

        // When & Then
        mockMvc.perform(put("/api/users/bulk-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTOs))
                        .with(SecurityMockMvcRequestPostProcessors.user(adminUserDetails)))
                .andExpect(status().isNotFound());  // Expecting 404 Not Found
    }

    @Test
    void testSoftDeleteUserSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        User user = UserFixture.getInstance().getDefaultUser();
        User softDeleted = new User(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(softDeleted);
        doNothing().when(userService).softDeleteUser(userId);

        UserDetails userDetails = withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/soft-delete", userId)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNoContent());// Expecting 204 No Content
    }

    @Test
    void testSoftDeleteUserNotFound() throws Exception {
        // Given
        Long nonExistentUserId = 1L;

        // Simulate that the user does not exist in the database
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Expect the service method to throw an exception when it tries to soft delete the non-existent user
        doThrow(new UserNotFoundException(nonExistentUserId)).when(userService).softDeleteUser(nonExistentUserId);

        UserDetails userDetails = withUsername("admin@example.com")
                .password("adminPassword")
                .roles("ADMIN")
                .build();

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/soft-delete", nonExistentUserId)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNotFound());
    }


    @Test
    void testBulkSoftDeleteUsersSuccessfully() throws Exception {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();

        User softDeletedUser1 = new User(user1.getId(), user1.getUsername(), user1.getPassword(), user1.getRole(), false);
        User softDeletedUser2 = new User(user2.getId(), user2.getUsername(), user2.getPassword(), user2.getRole(), false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(userRepository.save(user1)).thenReturn(softDeletedUser1);
        when(userRepository.save(user2)).thenReturn(softDeletedUser2);

        doNothing().when(userService).softDeleteUsers(userIds);

        UserDetails userDetails = withUsername(user1.getUsername())
                .password(user1.getPassword())
                .roles(user1.getRole().name())
                .build();

        // When & Then
        mockMvc.perform(patch("/api/users/bulk-soft-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNoContent());  // Expecting 204 No Content
    }

    @Test
    void testBulkSoftDeleteUsersNotFound() throws Exception {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);
        User user1 = UserFixture.getInstance().getDefaultUser();

        // Simulate that the second user does not exist in the database
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Expect the service method to throw an exception when it tries to soft delete the non-existent user
        doThrow(new UserNotFoundException(2L)).when(userService).softDeleteUsers(userIds);

        UserDetails userDetails = withUsername(user1.getUsername())
                .password(user1.getPassword())
                .roles(user1.getRole().name())
                .build();


        // When & Then
        mockMvc.perform(patch("/api/users/bulk-soft-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNotFound());  // Expecting 404 Not Found
    }


}
