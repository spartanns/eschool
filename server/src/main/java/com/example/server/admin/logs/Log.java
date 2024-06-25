package com.example.server.admin.logs;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Log {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private String filename;
    private String filetype;
    private @Lob byte[] data;

    public Log(String filename, String filetype, byte[] data) {
        this.filename = filename;
        this.filetype = filetype;
        this.data = data;
    }
}
