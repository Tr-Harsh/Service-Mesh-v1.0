package com.registryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    public static final Role USER = new Role("USER");
    public static final Role SERVICE = new Role("SERVICE");

    private String name;
}
