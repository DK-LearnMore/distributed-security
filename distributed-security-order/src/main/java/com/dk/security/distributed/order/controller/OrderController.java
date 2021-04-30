package com.dk.security.distributed.order.controller;

import com.dk.security.distributed.order.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @GetMapping("/r1")
    @PreAuthorize("hasAnyAuthority('p2')") // 之前的方法授权,表示这个方法只能被有p1权限的访问
    public String r1(){
        // 获取我们填充到security上下文的信息
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "访问资源1"+principal;
    }
}
