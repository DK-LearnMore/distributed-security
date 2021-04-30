package com.dk.security.distributed.uaa.entity;

import lombok.Data;

@Data
public class Permission {
    private String  id;
    private String code;
    private String description;
    private String url;
}
