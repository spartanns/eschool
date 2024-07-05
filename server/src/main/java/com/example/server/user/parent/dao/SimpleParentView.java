package com.example.server.user.parent.dao;

import com.example.server.security.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SimpleParentView {
    private @JsonView(Views.Public.class) Long id;
    private @JsonView(Views.Public.class) String name;
    private @JsonView(Views.Public.class) String surname;
    private @JsonView(Views.Public.class) String email;
}
