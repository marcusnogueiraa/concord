package com.concord.concordapi.fileStorage.dto;

import com.concord.concordapi.fileStorage.entity.FileType;

public record FileUploadResponseDto(
        String fileId,
        FileType type
) {
}