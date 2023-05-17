package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto create(UserDto userDto) {
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Transactional
    public UserDto findUserById(Long userId) {
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id = %d not found.", userId))));
    }

    @Transactional
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        User user = toUser(findUserById(userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
