package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    Optional<User> get(Long userId);

    List<User> getAll();

    User update(Long userId, User user);

    void delete(Long userId);

    boolean checkUserId(Long userId);

    Optional<User> findUserByEmail(String email);
}