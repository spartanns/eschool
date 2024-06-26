package com.example.server.admin.department.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data
public class DeptRequest {

    @NotNull(message = "Department must have a name.")
    @Size(min = 6, max = 6, message = "Department name must be exactly {max} characters long.")
    private String name;

}
