package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto create(UserDto userDto);

    public UserDto findUserById(Long userId);

    public List<UserDto> findAllUsers();

    public UserDto update(UserDto userDto, Long userId);

    public void delete(Long userId);
}

