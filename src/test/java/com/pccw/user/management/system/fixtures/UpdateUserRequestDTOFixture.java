package com.pccw.user.management.system.fixtures;

import com.pccw.usermanagementsystem.dto.UpdateUserRequestDTO;
import com.pccw.usermanagementsystem.entity.enums.Role;

// Singleton fixture class for UpdateUserRequestDTO entity
public class UpdateUserRequestDTOFixture {


    private static UpdateUserRequestDTOFixture instance;


    private UpdateUserRequestDTOFixture() {
    }

    public static UpdateUserRequestDTOFixture getInstance() {
        if (instance == null) {
            instance = new UpdateUserRequestDTOFixture();
        }
        return instance;
    }

    private static class UpdateUserRequestDTOBuilder {
        private Long id;
        private String username;
        private String password;
        private String role;
        private Boolean enabled;

        public UpdateUserRequestDTOBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UpdateUserRequestDTOBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UpdateUserRequestDTOBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UpdateUserRequestDTOBuilder withRole(String role) {
            this.role = role;
            return this;
        }

        public UpdateUserRequestDTOBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UpdateUserRequestDTO build() {
            return new UpdateUserRequestDTO(this.id, this.username, this.password, this.role, this.enabled);
        }
    }

    public UpdateUserRequestDTO getDefaultUpdateUserRequestDTO() {
        return new UpdateUserRequestDTOBuilder()
                .withId(1L)
                .withUsername("updated@example.com")
                .withPassword("updatedPassword")
                .withRole(Role.USER.getValue())
                .withEnabled(true)
                .build();
    }

    public UpdateUserRequestDTO getAdminUpdateUserRequestDTO() {
        return new UpdateUserRequestDTOBuilder()
                .withId(2L)
                .withUsername("admin_updated@example.com")
                .withPassword("adminUpdatedPassword")
                .withRole(Role.ADMIN.getValue())
                .withEnabled(true)
                .build();
    }
}
