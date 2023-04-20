package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getId() != null && userStorage.checkUserId(user.getId())) {
            throw new AlreadyExistsException("Пользователь уже существует");
        }
        if (userStorage.getAll().contains(user)) {
            throw new AlreadyExistsException("Пользователь уже существует");
        }
        user = userStorage.create(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(Long id) {

        User user = userStorage.get(id).orElseThrow(
                () -> new NotFoundException("Пользователь с этим идентификатором не найден.")
        );
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto update(Long userId, UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);

        if (!userStorage.checkUserId(userId)) {
            throw new NotFoundException("Пользователь с этим идентификатором не найден.");
        }
        User oldUser = UserMapper.toUser(getUserById(userId));
        Optional<User> dupleEmailUser = userStorage.findUserByEmail(newUser.getEmail());
        if (dupleEmailUser.isPresent() && !dupleEmailUser.get().getId().equals(userId)) {
            throw new AlreadyExistsException("Пользователь с этим адрессом эл.почты уже существует.");
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(userId, oldUser));
    }

    public void deleteUserById(Long userId) {
        if (!userStorage.checkUserId(userId)) {
            throw new NotFoundException("Пользователь с этим идентификатором не найден.");
        }
        userStorage.delete(userId);
    }
}
