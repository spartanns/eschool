package com.example.server.management;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/mgmt") @PreAuthorize("hasRole('MANAGER')")
public class ManagementController {
    private final ManagementService service;

    @PostMapping("/sendGlobalReport") @PreAuthorize("hasAuthority('manager:create')")
    ResponseEntity<String> sendGlobalReport() {
        try {
            return new ResponseEntity<String>(service.sendGlobalReport(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
