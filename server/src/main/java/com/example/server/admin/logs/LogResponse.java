package com.example.server.admin.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class LogResponse {
    private String filename;
    private String filetype;
    private String downloadURL;
    private long filesize;
}
