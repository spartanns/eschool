package com.example.server.admin.logs;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/logs") @PreAuthorize("hasRole('ADMIN')")
public class LogController {
    private final LogService service;

    @PostMapping("/upload") @PreAuthorize("hasAuthority('admin:create')")
    LogResponse uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Log log = service.saveLog(file);
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/logs/")
                .path(log.getId().toString())
                .toUriString();

        return new LogResponse(log.getFilename(), downloadURL, file.getContentType(), file.getSize());
    }

    @GetMapping("/download/{fileID}") @PreAuthorize("hasAuthority('admin:read')")
    ResponseEntity<Resource> downloadFile(@PathVariable Long fileID) {
        Log log = service.readLog(fileID);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(log.getFiletype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + log.getFilename() + "\"")
                .body(new ByteArrayResource(log.getData()));
    }
}
