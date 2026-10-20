package com.spotify.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestDTO(
        @NotBlank(message = "Artist name cannot be null") @Size(min = 4, max = 15, message = "Name can be between 4 and 15 size")
    String artistName,
    @NotBlank(message = "Song name cannot be null") @Size(min = 4, max = 30, message = "Song name has to be between 4 t0 30")
    String songName
) {}
