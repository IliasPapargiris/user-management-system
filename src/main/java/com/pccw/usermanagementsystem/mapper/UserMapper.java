package com.pccw.usermanagementsystem.mapper;

import com.pccw.usermanagementsystem.dto.UpdateUserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserRequestDTO;
import com.pccw.usermanagementsystem.dto.UserResponseDTO;
import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.entity.enums.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(userRequestDTO.getPassword());
        user.setRole(Role.fromValue(userRequestDTO.getRole()));
        user.setEnabled(userRequestDTO.getEnabled());
        return user;
    }


    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().getValue(),
                user.getEnabled()
        );
    }

    public List<UserResponseDTO> toDTOs(List<User> users) {

        return users.
                stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public User toEntity(UpdateUserRequestDTO updateUserRequestDTO) {
        if (updateUserRequestDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(updateUserRequestDTO.getId());
        user.setUsername(updateUserRequestDTO.getUsername());
        user.setPassword(updateUserRequestDTO.getPassword());
        user.setRole(Role.fromValue(updateUserRequestDTO.getRole()));
        user.setEnabled(updateUserRequestDTO.getEnabled());

        return user;
    }
}
