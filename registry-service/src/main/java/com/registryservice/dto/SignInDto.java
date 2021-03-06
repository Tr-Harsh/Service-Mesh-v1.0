package com.registryservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignInDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
