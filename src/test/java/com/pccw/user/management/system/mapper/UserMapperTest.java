package com.pccw.user.management.system.mapper;

import com.pccw.user.management.system.fixtures.UpdateUserRequestDTOFixture;
import com.pccw.user.management.system.fixtures.UserFixture;
import com.pccw.user.management.system.fixtures.UserRequestDTOFixture;
import com.pccw.usermanagementsystem.dto.UpdateUserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserResponseDTO;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = UserMapper.class)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;


    @Test
    void testToEntity() {
        // Given
        UserRequestDTO userRequestDTO = UserRequestDTOFixture.getInstance().getDefaultUserRequestDTO();

        // When
        User user = userMapper.toEntity(userRequestDTO);

        // Then
        assertNotNull(user);
        assertEquals(userRequestDTO.getUsername(), user.getUsername());
        assertEquals(userRequestDTO.getPassword(), user.getPassword());
        assertEquals(userRequestDTO.getRole(), user.getRole().getValue());
        assertEquals(userRequestDTO.getEnabled(), user.getEnabled());
    }

    @Test
    void testToEntityForUpdateUserRequestDTO() {
        // Given
        UpdateUserRequestDTO updateUserRequestDTO = UpdateUserRequestDTOFixture.getInstance().getDefaultUpdateUserRequestDTO();

        // When
        User user = userMapper.toEntity(updateUserRequestDTO);

        // Then
        assertNotNull(user);
        assertEquals(updateUserRequestDTO.getId(), user.getId());
        assertEquals(updateUserRequestDTO.getUsername(), user.getUsername());
        assertEquals(updateUserRequestDTO.getPassword(), user.getPassword());
        assertEquals(updateUserRequestDTO.getRole(), user.getRole().getValue());
        assertEquals(updateUserRequestDTO.getEnabled(), user.getEnabled());
    }

    @Test
    void testToDTO() {
        // Given
        User user = UserFixture.getInstance().getDefaultUser();

        // When
        UserResponseDTO userResponseDTO = userMapper.toDTO(user);

        // Then
        assertNotNull(userResponseDTO);
        assertEquals(user.getId(), userResponseDTO.getId());
        assertEquals(user.getUsername(), userResponseDTO.getUsername());
        assertEquals(user.getRole().getValue(), userResponseDTO.getRole());
        assertEquals(user.getEnabled(), userResponseDTO.getEnabled());
    }

    @Test
    void testToDTOs() {
        // Given
        User user1 = UserFixture.getInstance().getDefaultUser();
        User user2 = UserFixture.getInstance().getAdminUser();
        List<User> users = List.of(user1,user2);

        // When
        List<UserResponseDTO> userResponseDTOs = userMapper.toDTOs(users);

        // Then
        assertNotNull(userResponseDTOs);
        assertEquals(2, userResponseDTOs.size());

        UserResponseDTO userResponseDTO1 = userResponseDTOs.get(0);
        UserResponseDTO userResponseDTO2 = userResponseDTOs.get(1);

        assertEquals(user1.getId(), userResponseDTO1.getId());
        assertEquals(user1.getUsername(), userResponseDTO1.getUsername());
        assertEquals(user1.getRole().getValue(), userResponseDTO1.getRole());
        assertEquals(user1.getEnabled(), userResponseDTO1.getEnabled());

        assertEquals(user2.getId(), userResponseDTO2.getId());
        assertEquals(user2.getUsername(), userResponseDTO2.getUsername());
        assertEquals(user2.getRole().getValue(), userResponseDTO2.getRole());
        assertEquals(user2.getEnabled(), userResponseDTO2.getEnabled());
    }



}
