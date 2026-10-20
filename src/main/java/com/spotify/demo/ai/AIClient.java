package com.spotify.demo.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.util.Collections;

@Component
public class AIClient {
    private final RestClient restClient;

    public AIClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String roastAI(String artistName, String songName) {

        String prompt = "Roast the song '" + songName
                + "' by " + artistName
                + ". Be humorous, dark and give roast in Hinglish. Keep it under 30 words. "
                + "Return only the roast, no explanation.";

        Message message = new Message("user", prompt);
        GroqRequest requestBody = new GroqRequest("openai/gpt-oss-20b", Collections.singletonList(message));

        return restClient
               .post()
               .uri("/chat/completions")
               .contentType(MediaType.APPLICATION_JSON)
               .body(requestBody)
               .retrieve()
               .body(GroqResponse.class)
                .choices()
                .get(0).message().content();
    }
}
