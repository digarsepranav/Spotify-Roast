package com.spotify.demo.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Table(name = "roast")
@NoArgsConstructor
public class Roast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String artistName;
    private String songName;
    @Column(nullable = false)
    private String roastText;
    private LocalDateTime createdAt;
}
