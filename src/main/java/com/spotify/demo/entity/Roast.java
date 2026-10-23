package com.spotify.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Table(name = "roast")
@NoArgsConstructor
@Getter
public class Roast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String artistName;
    private String songName;
    @Column(nullable = false)
    private String roastText;
    private LocalDateTime createdAt;

    public void setId(long id) {
        this.id = id;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }
    public void setRoastText(String roastText) {
        this.roastText = roastText;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
