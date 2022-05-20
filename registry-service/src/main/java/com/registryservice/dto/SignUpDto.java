package com.registryservice.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignUpDto {
    @NotBlank
    @Size(min = 3, max = 15)
    public String username;

    @NotBlank
    @Size(min = 6, max = 20)
    public String password;

    @NotBlank
    @Size(max = 40)
    @Email
    public String email;
}
