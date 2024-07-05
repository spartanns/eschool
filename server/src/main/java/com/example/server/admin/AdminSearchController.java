package com.example.server.admin;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/admin/search") @PreAuthorize("hasRole('ADMIN')")
public class AdminSearchController {
    private final AdminSearchService service;

    @GetMapping @PreAuthorize("hasAuthority('admin:read')") @JsonView(Views.Public.class)
    ResponseEntity<?> search(@RequestParam String entity, @RequestParam String filterBy, @RequestParam String query) {
        try {
            return new ResponseEntity<List<?>>(service.search(entity, filterBy, query), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
