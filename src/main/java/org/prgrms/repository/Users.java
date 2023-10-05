package org.prgrms.repository;

import java.util.UUID;

public class Users {
    private final UUID id;
    private String name;

    public Users(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
    }
}