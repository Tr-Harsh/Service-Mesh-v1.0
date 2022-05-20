package com.registryservice.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@RequiredArgsConstructor
public class JWTAuthResponseDto {

    @NonNull
    private String accessToken;
    private String tokenType = "Bearer";
}
