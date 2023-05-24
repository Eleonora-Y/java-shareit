package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    public ItemRequestDto findById(Long userId, Long requestId);

    public List<ItemRequestDto> findAllRequests(Long userId, int from, int size);

    public List<ItemRequestDto> findAllUserRequests(Long userId);
}
