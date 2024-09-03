package com.pccw.usermanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.pccw.usermanagementsystem.repository")
public class UserManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.pccw.usermanagementsystem.UserManagementSystemApplication.class, args);
    }
}
