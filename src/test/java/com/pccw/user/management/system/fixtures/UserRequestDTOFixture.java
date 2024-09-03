package com.pccw.user.management.system.fixtures;
import com.pccw.usermanagementsystem.dto.UserRequestDTO;
import com.pccw.usermanagementsystem.entity.enums.Role;

public class UserRequestDTOFixture {

    private static UserRequestDTOFixture instance;

    private UserRequestDTOFixture() {
    }

    public static UserRequestDTOFixture getInstance() {
        if (instance == null) {
            instance = new UserRequestDTOFixture();
        }
        return instance;
    }

    private static class UserRequestDTOBuilder {
        private String username;
        private String password;
        private String role;
        private Boolean enabled;

        public UserRequestDTOBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserRequestDTOBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserRequestDTOBuilder withRole(String role) {
            this.role = role;
            return this;
        }

        public UserRequestDTOBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserRequestDTO build() {
            return new UserRequestDTO(this.username, this.password, this.role, this.enabled);
        }
    }

    public UserRequestDTO getDefaultUserRequestDTO() {
        return new UserRequestDTOBuilder()
                .withUsername("default@example.com")
                .withPassword("defaultPassword")
                .withRole(Role.USER.getValue())
                .withEnabled(true)
                .build();
    }

    public UserRequestDTO getAdminUserRequestDTO() {
        return new UserRequestDTOBuilder()
                .withUsername("admin@example.com")
                .withPassword("adminPassword")
                .withRole(Role.ADMIN.getValue())
                .withEnabled(true)
                .build();
    }
}
