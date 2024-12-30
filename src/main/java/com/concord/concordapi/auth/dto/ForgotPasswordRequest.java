package com.concord.concordapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ForgotPasswordRequest(
    @NotNull(message = "Email is required")
    @Email
    String email
) {}
