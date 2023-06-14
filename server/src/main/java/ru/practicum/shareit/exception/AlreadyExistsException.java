package ru.practicum.shareit.exception;

public class AlreadyExistsException extends BadRequestException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
