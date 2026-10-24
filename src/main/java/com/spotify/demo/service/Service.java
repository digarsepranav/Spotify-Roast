package com.spotify.demo.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spotify.demo.ai.AIClient;
import com.spotify.demo.dto.RequestDTO;
import com.spotify.demo.dto.ResponseDTO;
import com.spotify.demo.entity.Roast;
import com.spotify.demo.repository.RoastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service {

    private final AIClient aiClient;
    private final RoastRepository repository;

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
        Roast roast = new Roast();
        ResponseDTO dto = new ResponseDTO(artistName, songName, aiClient.roastAI(artistName, songName));

        roast.setArtistName(dto.artistName());
        roast.setSongName(dto.songName());
        roast.setRoastText(dto.roast());
        roast.setCreatedAt(LocalDateTime.now());

        repository.save(roast);

        return dto;
    }

    public Page<ResponseDTO> getHistory(Pageable pageable) {
        Page<Roast> ldto = repository.findAll(pageable);
        Page<ResponseDTO> rdto;

        rdto = ldto.map(roast ->
            new ResponseDTO(
                    roast.getArtistName(),
                    roast.getSongName(),
                    roast.getRoastText()
            ));
        return rdto;
    }
}
