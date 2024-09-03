package com.pccw.user.management.system.service;

import com.pccw.user.management.system.fixtures.UserFixture;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.exception.UserAlreadyExistsException;
import com.pccw.usermanagementsystem.exception.UserNotFoundException;
import com.pccw.usermanagementsystem.repository.UserRepository;
import com.pccw.usermanagementsystem.service.UserService;
import com.pccw.usermanagementsystem.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserService.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @Test
    void testRegisterUserSuccessfully() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User savedUser = userService.registerUser(user);

        // Then
        assertNotNull(savedUser);
        assertEquals(user.getPassword(), savedUser.getPassword());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendWelcomeEmail(eq(user.getUsername()), anyString(), anyString());
    }

    @Test
    void testRegisterAlreadyExistingUser() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));

        verify(userRepository, times(1)).findByUsername(user.getUsername());

        verify(userRepository, never()).save(any(User.class));

        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testFindUserByUsername() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();


        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // When
        Optional<User> expected = userService.findUserByUsername(user.getUsername());

        // Then
        assertTrue(expected.isPresent());
        assertEquals(user.getUsername(), expected.get().getUsername());
    }

    @Test
    void testUpdateUserSuccessfully() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();
        User updatedUser = new User(user.getId(), "updatedUsername", "updatedPassword", user.getRole(), true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn(updatedUser.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getPassword(), result.getPassword());
        assertEquals(updatedUser.getRole(), result.getRole());
        assertEquals(updatedUser.getEnabled(), result.getEnabled());

        verify(userRepository, times(1)).findById(user.getId());

        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateNonExistingUser() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void testSoftDeleteUserSuccessfully() {
        // Given
        User existingUser =UserFixture.getInstance().getDefaultUser();
        existingUser.setEnabled(true);

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        // When
        userService.softDeleteUser(existingUser.getId());

        // Then
        assertFalse(existingUser.getEnabled());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testSoftDeleteUserNonExistingUser() {
        // Given
        User nonExistingUser = UserFixture.getInstance().getDefaultUser();
        when(userRepository.findById(nonExistingUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.softDeleteUser(nonExistingUser.getId()));
    }

    @Test
    void testBulkRegisterUsersSuccessfully() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();
        List<User> users = Arrays.asList(user1, user2);
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

        when(userRepository.findByUsernameIn(usernames)).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.saveAll(anyList())).thenReturn(users);

        // When
        List<User> expected = userService.registerUsers(users);

        // Then
        assertNotNull(expected);
        assertEquals(2, expected.size());
        assertEquals(users, expected);
        verify(userRepository, times(1)).findByUsernameIn(usernames);
        verify(userRepository, times(1)).saveAll(users);
        verify(emailService, times(1)).sendWelcomeEmail(eq(user1.getUsername()), anyString(), anyString());
        verify(emailService, times(1)).sendWelcomeEmail(eq(user2.getUsername()), anyString(), anyString());
    }

    @Test
    void testUnsuccessfulBulkRegisterUsers() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();
        List<User> users = Arrays.asList(user1, user2);
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());


        when(userRepository.findByUsernameIn(usernames)).thenReturn(List.of(user1));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUsers(users));


        // Verifications
        verify(userRepository, times(1)).findByUsernameIn(usernames);
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }


    @Test
    void testUpdateBulkUsersSuccessfully() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        user1.setPassword("oldPassword1");

        User user2 = UserFixture.getInstance().getAdminUser();
        user2.setPassword("oldPassword2");

        List<User> existingUsers = Arrays.asList(user1, user2);
        List<User> updatedUsers = Arrays.asList(
                new User(user1.getId(), "updatedUser1@example.com", "newPassword1", user1.getRole(), true),
                new User(user2.getId(), "updatedUser2@example.com", "newPassword2", user2.getRole(), true)
        );

        List<Long> existingIds = existingUsers.stream()
                .map(User::getId)
                .toList();

        when(userRepository.findAllById(existingIds)).thenReturn(existingUsers);
        when(passwordEncoder.encode(user1.getPassword())).thenReturn("encoded");
        when(passwordEncoder.encode(user2.getPassword())).thenReturn("encoded");
        when(userRepository.saveAll(existingUsers)).thenReturn(updatedUsers);

        // When
        List<User> expected = userService.updateUsers(updatedUsers);

        // Then
        assertNotNull(expected);
        assertEquals(expected, updatedUsers);

        User expectedUser1 = expected.get(0);
        User expectedUser2 = expected.get(1);

        User updatedUser1 = updatedUsers.get(0);
        User updatedUser2 = updatedUsers.get(1);

        assertEquals(updatedUser1.getUsername(), expectedUser1.getUsername());
        assertEquals(updatedUser1.getPassword(), expectedUser1.getPassword());
        assertEquals(updatedUser1.getRole(), expectedUser1.getRole());
        assertTrue(updatedUser1.getEnabled());

        assertEquals(updatedUser2.getUsername(), expectedUser2.getUsername());
        assertEquals(updatedUser2.getPassword(), expectedUser2.getPassword());
        assertEquals(updatedUser2.getRole(), expectedUser2.getRole());
        assertTrue(expectedUser2.getEnabled());

        //Verification
        verify(userRepository, times(1)).saveAll(updatedUsers);
    }

    @Test
    void testUnsuccessfulBulkUpdateUsers() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        user1.setPassword("oldPassword1");

        User user2 = UserFixture.getInstance().getAdminUser();
        user2.setPassword("oldPassword2");

        List<User> existingUsers = List.of(user1);  // Simulating only one user is found
        List<User> updatedUsers = Arrays.asList(
                new User(user1.getId(), "updatedUser1@example.com", "newPassword1", user1.getRole(), true),
                new User(user2.getId(), "updatedUser2@example.com", "newPassword2", user2.getRole(), true)
        );

        List<Long> existingIds = Stream.of(user1,user2)
                .map(User::getId)
                .toList();

        when(userRepository.findAllById(existingIds)).thenReturn(existingUsers);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUsers(updatedUsers));

        //Verification
        verify(userRepository, times(1)).findAllById(existingIds);
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void testUnsuccessfulBulkSoftDeleteUsers() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();

        List<Long> userIds = Arrays.asList(user1.getId(), user2.getId());
        List<User> foundUsers = List.of(user1);  // Simulating only one user is found

        when(userRepository.findAllById(userIds)).thenReturn(foundUsers);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.softDeleteUsers(userIds));

        //Verification
        verify(userRepository, times(1)).findAllById(userIds);
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void testSuccessfulBulkSoftDeleteUsers() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();

        List<Long> userIds = Arrays.asList(user1.getId(), user2.getId());
        List<User> foundUsers = Arrays.asList(user1, user2);

        when(userRepository.findAllById(userIds)).thenReturn(foundUsers);
        foundUsers.forEach(user -> user.setEnabled(false));
        when(userRepository.saveAll(foundUsers)).thenReturn(foundUsers);

        // When
        userService.softDeleteUsers(userIds);

        // Then
        // Verify that the repository's findAllById method was called with the correct user IDs
        verify(userRepository, times(1)).findAllById(userIds);

        // Verification
        verify(userRepository, times(1)).saveAll(foundUsers);

        // Assert that the users were indeed soft deleted (enabled set to false)
        assertFalse(foundUsers.get(0).getEnabled());
        assertFalse(foundUsers.get(1).getEnabled());
    }


    @Test
    void testFindExistingUsernames() {
        // Given
        List<String> usernames = Arrays.asList("user1", "user2", "user3");

        User user1 = UserFixture.getInstance().getDefaultUser();
        user1.setUsername("user1");

        User user3 = UserFixture.getInstance().getAdminUser();
        user3.setUsername("user3");

        List<User> existingUsers = Arrays.asList(user1, user3);

        when(userRepository.findByUsernameIn(usernames)).thenReturn(existingUsers);

        // When
        List<String> existingUsernames = userService.findExistingUsernames(usernames);

        // Then
        assertEquals(2, existingUsernames.size());
        assertEquals(Arrays.asList("user1", "user3"), existingUsernames);
        verify(userRepository, times(1)).findByUsernameIn(usernames);
    }

}
