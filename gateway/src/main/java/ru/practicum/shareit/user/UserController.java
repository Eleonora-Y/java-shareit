package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                             @PathVariable("id") Long userId) {
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId) {
        return userClient.deleteUser(userId);
    }
}