package com.pccw.usermanagementsystem.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Getter
@RequiredArgsConstructor
public enum Role {
    USER("user"),
    ADMIN("admin");

    private final String value;

//    Role(String value) {
//        this.value = value;
//    }

//    public String getValue() {
//        return value;
//    }

    private static final Map<String, Role> ROLE_MAP = new HashMap<>();

    // Static block to populate the HashMap
    static {
        for (Role role : Role.values()) {
            ROLE_MAP.put(role.getValue().toLowerCase(), role);
        }
    }

    public static Role fromValue(String value) {
        Role role = ROLE_MAP.get(value.toLowerCase());
        if (role == null) {
            throw new IllegalArgumentException("Unknown role value: " + value);
        }
        return role;
    }
}
