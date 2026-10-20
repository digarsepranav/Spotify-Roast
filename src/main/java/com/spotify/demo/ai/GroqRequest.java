package com.spotify.demo.ai;

import java.util.List;

public record GroqRequest(
        String model,
        List<Message> messages
) {
}
