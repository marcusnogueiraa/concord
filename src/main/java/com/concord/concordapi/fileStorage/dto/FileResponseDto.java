package com.concord.concordapi.fileStorage.dto;

import java.util.UUID;

import com.concord.concordapi.fileStorage.entity.FileType;

public record FileResponseDto(
        UUID id,
        String url,
        FileType type
) {
}