package com.spotify.demo.service;

import com.spotify.demo.ai.AIClient;
import com.spotify.demo.dto.RequestDTO;
import com.spotify.demo.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service {

    private final AIClient aiClient;

    ArrayList<String> roastList = new ArrayList<>(List.of(
            "Your music taste is straight outta 2017",
            "Even spotify want to skip your song",
            "Your playlist needs a professional touch"
    ));

    public ResponseDTO roastService(RequestDTO requestDTO) {
        String songName = requestDTO.songName();
        String artistName = requestDTO.artistName();
        int size = roastList.size();
        int randomNumber = ThreadLocalRandom.current().nextInt(0, size);

        return new ResponseDTO(artistName, songName, roastList.get(randomNumber));
    }
    public ResponseDTO roastAIService(RequestDTO requestDTO) {
        String songName = requestDTO.songName();
        String artistName = requestDTO.artistName();
        return new ResponseDTO(artistName, songName, aiClient.roastAI(artistName, songName));
    }
}
