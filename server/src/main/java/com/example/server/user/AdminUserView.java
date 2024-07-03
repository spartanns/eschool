package com.example.server.user;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class AdminUserView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String username;
    private @JsonView(Views.Public.class) Role role;
}
