package com.dk.security.distributed.uaa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private BigDecimal id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
