package com.concord.concordapi.auth.dto;

import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
    @NotNull(message = "newPassword is required")
    String newPassword
) {}
