package com.project.trading.model;

public class AuthUser {
    private final long id;
    private final String username;
    private final Role role;

    public AuthUser(long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
