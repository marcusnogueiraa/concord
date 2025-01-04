package com.concord.concordapi.server.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServerPutBodyDTO(
    
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    String name,
    String imageTempPath

) {}