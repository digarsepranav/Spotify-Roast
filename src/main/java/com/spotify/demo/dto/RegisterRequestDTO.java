package com.spotify.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    @NotBlank @NotNull
    String username;
    @NotBlank @NotNull
    String email;
    @NotBlank @NotNull
    String password;
}
