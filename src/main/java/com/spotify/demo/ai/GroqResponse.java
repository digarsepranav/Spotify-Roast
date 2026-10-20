package com.spotify.demo.ai;

import java.util.List;

public record GroqResponse(List<Choices> choices) {
}
