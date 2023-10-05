package org.prgrms.repository;

import java.util.List;
import java.util.UUID;

public interface UsersRepository {

    Users save(Users users);

    List<Users> findAll();

    void update(UUID id, String name);

    void deleteAll();
}