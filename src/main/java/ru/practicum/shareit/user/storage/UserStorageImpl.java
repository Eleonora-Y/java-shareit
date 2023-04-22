package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserStorageImpl implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        if (user.getId() == null) {
            user.setId(++id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> get(Long userId) {
        return users.values().stream().filter(user -> userId.equals(user.getId())).findFirst();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(Long userId, User user) {
        users.replace(userId, user);
        user.setId(userId);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean checkUserId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}







