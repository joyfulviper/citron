package org.prgrms.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UsersRepositoryImpl implements UsersRepository{

    private final Map<UUID, Users> database;

    public UsersRepositoryImpl(Map<UUID, Users> database) {
        this.database = database;
    }

    @Override
    public Users save(Users users) {
        return database.put(users.getId(), users);
    }

    @Override
    public List<Users> findAll() {
        return database.values().stream().toList();
    }

    @Override
    public void update(UUID id, String name) {
        var user = database.get(id);
        user.update(name);

    }

    @Override
    public void deleteAll() {
        database.clear();
    }
}