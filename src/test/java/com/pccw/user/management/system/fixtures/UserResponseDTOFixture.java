package com.pccw.user.management.system.fixtures;
import com.pccw.usermanagementsystem.dto.UserResponseDTO;
import com.pccw.usermanagementsystem.entity.enums.Role;

public class UserResponseDTOFixture {

    private static UserResponseDTOFixture instance;

    private UserResponseDTOFixture() {}

    public static UserResponseDTOFixture getInstance() {
        if (instance == null) {
            instance = new UserResponseDTOFixture();
        }
        return instance;
    }

    private static class UserResponseDTOBuilder {
        private Long id;
        private String username;
        private String role;
        private Boolean enabled;

        public UserResponseDTOBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserResponseDTOBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserResponseDTOBuilder withRole(Role role) {
            this.role = role.name();
            return this;
        }

        public UserResponseDTOBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserResponseDTO build() {
            return new UserResponseDTO(this.id, this.username, this.role, this.enabled);
        }
    }

    public UserResponseDTO getDefaultUserResponseDTO() {
        return new UserResponseDTOBuilder()
                .withId(1L)
                .withUsername("default@example.com")
                .withRole(Role.USER)
                .withEnabled(true)
                .build();
    }

    public UserResponseDTO getAdminUserResponseDTO() {
        return new UserResponseDTOBuilder()
                .withId(2L)
                .withUsername("admin@example.com")
                .withRole(Role.ADMIN)
                .withEnabled(true)
                .build();
    }
}
