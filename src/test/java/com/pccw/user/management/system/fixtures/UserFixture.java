package com.pccw.user.management.system.fixtures;

import com.pccw.usermanagementsystem.entity.User;
import com.pccw.usermanagementsystem.entity.enums.Role;

public class UserFixture {

    private static UserFixture instance;

    private UserFixture() {}

    public static UserFixture getInstance() {
        if (instance == null) {
            instance = new UserFixture();
        }
        return instance;
    }

    private static class UserBuilder {
        private Long id;
        private String username;
        private String password;
        private Role role;
        private boolean enabled;

        public UserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setUsername(this.username);
            user.setPassword(this.password);
            user.setRole(this.role);
            user.setEnabled(this.enabled);
            return user;
        }
    }

    public User getDefaultUser() {
        return new UserBuilder()
                .withId(1L)
                .withUsername("default@example.com")
                .withPassword("defaultPassword")
                .withRole(Role.USER)
                .withEnabled(true)
                .build();
    }

    public User getAdminUser() {
        return new UserBuilder()
                .withId(2L)
                .withUsername("admin@example.com")
                .withPassword("adminPassword")
                .withRole(Role.ADMIN)
                .withEnabled(true)
                .build();
    }
}