package com.spotify.demo.controller;
import com.spotify.demo.dto.RequestDTO;
import com.spotify.demo.dto.ResponseDTO;
import com.spotify.demo.service.Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RestControllerAdvice("404 Bad Request")
public class Controller {
    private final Service service;

    @PostMapping("/api/roast")
    public ResponseEntity<ResponseDTO> roast(@RequestBody @Valid RequestDTO requestDTO) {
        return ResponseEntity.ok(service.roastService(requestDTO));
    }
    @PostMapping("/api/ai/roast")
    public ResponseEntity<ResponseDTO> roastAI(@RequestBody @Valid RequestDTO requestDTO) {
        return ResponseEntity.ok(service.roastAIService(requestDTO));
    }
    @GetMapping("/api/ai/history")
    public ResponseEntity<Page<ResponseDTO>> getALl(Pageable pageable) {
        return ResponseEntity.ok(service.getHistory((org.springframework.data.domain.Pageable) pageable));
    }
}
